package noppes.npcs.quests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;

public class QuestItem extends QuestInterface {
   public NpcMiscInventory items = new NpcMiscInventory(3);
   public boolean leaveItems = false;
   public boolean ignoreDamage = false;
   public boolean ignoreNBT = false;

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.items.setFromNBT(compound.getCompoundTag("Items"));
      this.leaveItems = compound.getBoolean("LeaveItems");
      this.ignoreDamage = compound.getBoolean("IgnoreDamage");
      this.ignoreNBT = compound.getBoolean("IgnoreNBT");
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setTag("Items", this.items.getToNBT());
      compound.setBoolean("LeaveItems", this.leaveItems);
      compound.setBoolean("IgnoreDamage", this.ignoreDamage);
      compound.setBoolean("IgnoreNBT", this.ignoreNBT);
   }

   public boolean isCompleted(EntityPlayer player) {
      for(ItemStack reqItem : NoppesUtilPlayer.countStacks(this.items, this.ignoreDamage, this.ignoreNBT)) {
         if (!NoppesUtilPlayer.compareItems(player, reqItem, this.ignoreDamage, this.ignoreNBT)) {
            return false;
         }
      }

      return true;
   }

   public Map<ItemStack, Integer> getProgressSet(EntityPlayer player) {
      HashMap<ItemStack, Integer> map = new HashMap();

      for(ItemStack item : NoppesUtilPlayer.countStacks(this.items, this.ignoreDamage, this.ignoreNBT)) {
         if (!NoppesUtilServer.IsItemStackNull(item)) {
            map.put(item, Integer.valueOf(0));
         }
      }

      for(ItemStack item : player.inventory.mainInventory) {
         if (!NoppesUtilServer.IsItemStackNull(item)) {
            for(Entry<ItemStack, Integer> questItem : map.entrySet()) {
               if (NoppesUtilPlayer.compareItems((ItemStack)questItem.getKey(), item, this.ignoreDamage, this.ignoreNBT)) {
                  map.put(questItem.getKey(), Integer.valueOf(((Integer)questItem.getValue()).intValue() + item.getCount()));
               }
            }
         }
      }

      return map;
   }

   public void handleComplete(EntityPlayer player) {
      if (!this.leaveItems) {
         for(ItemStack questitem : this.items.items) {
            if (!questitem.isEmpty()) {
               int stacksize = questitem.getCount();

               for(int i = 0; i < player.inventory.mainInventory.size(); ++i) {
                  ItemStack item = (ItemStack)player.inventory.mainInventory.get(i);
                  if (!NoppesUtilServer.IsItemStackNull(item) && NoppesUtilPlayer.compareItems(item, questitem, this.ignoreDamage, this.ignoreNBT)) {
                     int size = item.getCount();
                     if (stacksize - size >= 0) {
                        player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        item.splitStack(size);
                     } else {
                        item.splitStack(stacksize);
                     }

                     stacksize -= size;
                     if (stacksize <= 0) {
                        break;
                     }
                  }
               }
            }
         }

      }
   }

   public String[] getQuestLogStatus(EntityPlayer player) {
      Vector<String> vec = new Vector();
      List<ItemStack> questItems = NoppesUtilPlayer.countStacks(this.items, this.ignoreDamage, this.ignoreNBT);
      Map<ItemStack, Integer> progress = this.getProgressSet(player);

      for(int slot = 0; slot < this.items.items.size(); ++slot) {
         ItemStack quest = (ItemStack)this.items.items.get(slot);
         if (!quest.isEmpty()) {
            ItemStack item = ItemStack.EMPTY;

            for(ItemStack is : progress.keySet()) {
               if (NoppesUtilPlayer.compareItems(quest, is, this.ignoreDamage, this.ignoreNBT)) {
                  item = is;
                  break;
               }
            }

            int count = 0;
            if (progress.containsKey(item)) {
               count = ((Integer)progress.get(item)).intValue();
            }

            String process = count + "";
            if (count >= quest.getCount()) {
               process = quest.getCount() + "";
            }

            item.setCount(count - quest.getCount());
            process = process + "/" + quest.getCount() + "";
            if (quest.hasDisplayName()) {
               vec.add(quest.getDisplayName() + ": " + process);
            } else {
               vec.add(quest.getTranslationKey() + ".name: " + process);
            }
         }
      }

      return (String[])vec.toArray(new String[vec.size()]);
   }
}
