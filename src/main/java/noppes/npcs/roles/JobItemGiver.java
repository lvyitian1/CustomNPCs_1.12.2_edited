package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.controllers.GlobalDataController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerItemGiverData;
import noppes.npcs.entity.EntityNPCInterface;

public class JobItemGiver extends JobInterface {
   public int cooldownType = 0;
   public int givingMethod = 0;
   public int cooldown = 10;
   public NpcMiscInventory inventory = new NpcMiscInventory(9);
   public int itemGiverId = 0;
   public List<String> lines = new ArrayList();
   private int ticks = 10;
   private List<EntityPlayer> recentlyChecked = new ArrayList();
   private List<EntityPlayer> toCheck;
   public Availability availability = new Availability();

   public JobItemGiver(EntityNPCInterface npc) {
      super(npc);
      this.lines.add("Have these items {player}");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
      nbttagcompound.setInteger("igCooldownType", this.cooldownType);
      nbttagcompound.setInteger("igGivingMethod", this.givingMethod);
      nbttagcompound.setInteger("igCooldown", this.cooldown);
      nbttagcompound.setInteger("ItemGiverId", this.itemGiverId);
      nbttagcompound.setTag("igLines", NBTTags.nbtStringList(this.lines));
      nbttagcompound.setTag("igJobInventory", this.inventory.getToNBT());
      nbttagcompound.setTag("igAvailability", this.availability.writeToNBT(new NBTTagCompound()));
      return nbttagcompound;
   }

   public void readFromNBT(NBTTagCompound nbttagcompound) {
      this.itemGiverId = nbttagcompound.getInteger("ItemGiverId");
      this.cooldownType = nbttagcompound.getInteger("igCooldownType");
      this.givingMethod = nbttagcompound.getInteger("igGivingMethod");
      this.cooldown = nbttagcompound.getInteger("igCooldown");
      this.lines = NBTTags.getStringList(nbttagcompound.getTagList("igLines", 10));
      this.inventory.setFromNBT(nbttagcompound.getCompoundTag("igJobInventory"));
      if (this.itemGiverId == 0 && GlobalDataController.instance != null) {
         this.itemGiverId = GlobalDataController.instance.incrementItemGiverId();
      }

      this.availability.readFromNBT(nbttagcompound.getCompoundTag("igAvailability"));
   }

   public NBTTagList newHashMapNBTList(HashMap<String, Long> lines) {
      NBTTagList nbttaglist = new NBTTagList();

      for(String s : lines.keySet()) {
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setString("Line", s);
         nbttagcompound.setLong("Time", ((Long)lines.get(s)).longValue());
         nbttaglist.appendTag(nbttagcompound);
      }

      return nbttaglist;
   }

   public HashMap<String, Long> getNBTLines(NBTTagList tagList) {
      HashMap<String, Long> map = new HashMap();

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
         String line = nbttagcompound.getString("Line");
         long time = nbttagcompound.getLong("Time");
         map.put(line, Long.valueOf(time));
      }

      return map;
   }

   private boolean giveItems(EntityPlayer player) {
      PlayerItemGiverData data = PlayerData.get(player).itemgiverData;
      if (!this.canPlayerInteract(data)) {
         return false;
      } else {
         Vector<ItemStack> items = new Vector();
         Vector<ItemStack> toGive = new Vector();

         for(ItemStack is : this.inventory.items) {
            if (!is.isEmpty()) {
               items.add(is.copy());
            }
         }

         if (items.isEmpty()) {
            return false;
         } else {
            if (this.isAllGiver()) {
               toGive = items;
            } else if (this.isRemainingGiver()) {
               for(ItemStack is : items) {
                  if (!this.playerHasItem(player, is.getItem())) {
                     toGive.add(is);
                  }
               }
            } else if (this.isRandomGiver()) {
               toGive.add(((ItemStack)items.get(this.npc.world.rand.nextInt(items.size()))).copy());
            } else if (this.isGiverWhenNotOwnedAny()) {
               boolean ownsItems = false;

               for(ItemStack is : items) {
                  if (this.playerHasItem(player, is.getItem())) {
                     ownsItems = true;
                     break;
                  }
               }

               if (ownsItems) {
                  return false;
               }

               toGive = items;
            } else if (this.isChainedGiver()) {
               int itemIndex = data.getItemIndex(this);
               int i = 0;

               for(ItemStack item : this.inventory.items) {
                  if (i == itemIndex) {
                     toGive.add(item);
                     break;
                  }

                  ++i;
               }
            }

            if (toGive.isEmpty()) {
               return false;
            } else if (this.givePlayerItems(player, toGive)) {
               if (!this.lines.isEmpty()) {
                  this.npc.say(player, new Line((String)this.lines.get(this.npc.getRNG().nextInt(this.lines.size()))));
               }

               if (this.isDaily()) {
                  data.setTime(this, (long)this.getDay());
               } else {
                  data.setTime(this, System.currentTimeMillis());
               }

               if (this.isChainedGiver()) {
                  data.setItemIndex(this, (data.getItemIndex(this) + 1) % this.inventory.items.size());
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   private int getDay() {
      return (int)(this.npc.world.getTotalWorldTime() / 24000L);
   }

   private boolean canPlayerInteract(PlayerItemGiverData data) {
      if (this.inventory.items.isEmpty()) {
         return false;
      } else if (this.isOnTimer()) {
         if (!data.hasInteractedBefore(this)) {
            return true;
         } else {
            return data.getTime(this) + (long)(this.cooldown * 1000) < System.currentTimeMillis();
         }
      } else if (this.isGiveOnce()) {
         return !data.hasInteractedBefore(this);
      } else if (this.isDaily()) {
         if (!data.hasInteractedBefore(this)) {
            return true;
         } else {
            return (long)this.getDay() > data.getTime(this);
         }
      } else {
         return false;
      }
   }

   private boolean givePlayerItems(EntityPlayer player, Vector<ItemStack> toGive) {
      if (toGive.isEmpty()) {
         return false;
      } else if (this.freeInventorySlots(player) < toGive.size()) {
         return false;
      } else {
         for(ItemStack is : toGive) {
            this.npc.givePlayerItem(player, is);
         }

         return true;
      }
   }

   private boolean playerHasItem(EntityPlayer player, Item item) {
      for(ItemStack is : player.inventory.mainInventory) {
         if (!is.isEmpty() && is.getItem() == item) {
            return true;
         }
      }

      for(ItemStack is : player.inventory.armorInventory) {
         if (!is.isEmpty() && is.getItem() == item) {
            return true;
         }
      }

      return false;
   }

   private int freeInventorySlots(EntityPlayer player) {
      int i = 0;

      for(ItemStack is : player.inventory.mainInventory) {
         if (NoppesUtilServer.IsItemStackNull(is)) {
            ++i;
         }
      }

      return i;
   }

   private boolean isRandomGiver() {
      return this.givingMethod == 0;
   }

   private boolean isAllGiver() {
      return this.givingMethod == 1;
   }

   private boolean isRemainingGiver() {
      return this.givingMethod == 2;
   }

   private boolean isGiverWhenNotOwnedAny() {
      return this.givingMethod == 3;
   }

   private boolean isChainedGiver() {
      return this.givingMethod == 4;
   }

   public boolean isOnTimer() {
      return this.cooldownType == 0;
   }

   private boolean isGiveOnce() {
      return this.cooldownType == 1;
   }

   private boolean isDaily() {
      return this.cooldownType == 2;
   }

   public boolean aiShouldExecute() {
      if (this.npc.isAttacking()) {
         return false;
      } else {
         --this.ticks;
         if (this.ticks > 0) {
            return false;
         } else {
            this.ticks = 10;
            this.toCheck = this.npc.world.getEntitiesWithinAABB(EntityPlayer.class, this.npc.getEntityBoundingBox().grow(3.0D, 3.0D, 3.0D));
            this.toCheck.removeAll(this.recentlyChecked);
            List<EntityPlayer> listMax = this.npc.world.getEntitiesWithinAABB(EntityPlayer.class, this.npc.getEntityBoundingBox().grow(10.0D, 10.0D, 10.0D));
            this.recentlyChecked.retainAll(listMax);
            this.recentlyChecked.addAll(this.toCheck);
            return this.toCheck.size() > 0;
         }
      }
   }

   public boolean aiContinueExecute() {
      return false;
   }

   public void aiStartExecuting() {
      for(EntityPlayer player : this.toCheck) {
         if (this.npc.canSee(player) && this.availability.isAvailable(player)) {
            this.recentlyChecked.add(player);
            this.interact(player);
         }
      }

   }

   public void killed() {
   }

   private boolean interact(EntityPlayer player) {
      if (!this.giveItems(player)) {
         this.npc.say(player, this.npc.advanced.getInteractLine());
      }

      return true;
   }

   public void delete() {
   }
}
