package noppes.npcs.controllers.data;

import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.controllers.BankController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.CustomNPCsScheduler;

public class BankData {
   public HashMap<Integer, NpcMiscInventory> itemSlots = new HashMap();
   public HashMap<Integer, Boolean> upgradedSlots = new HashMap();
   public int unlockedSlots = 0;
   public int bankId = -1;

   public BankData() {
      for(int i = 0; i < 6; ++i) {
         this.itemSlots.put(Integer.valueOf(i), new NpcMiscInventory(54));
         this.upgradedSlots.put(Integer.valueOf(i), Boolean.valueOf(false));
      }

   }

   public void readNBT(NBTTagCompound nbttagcompound) {
      this.bankId = nbttagcompound.getInteger("DataBankId");
      this.unlockedSlots = nbttagcompound.getInteger("UnlockedSlots");
      this.itemSlots = this.getItemSlots(nbttagcompound.getTagList("BankInv", 10));
      this.upgradedSlots = NBTTags.getBooleanList(nbttagcompound.getTagList("UpdatedSlots", 10));
   }

   private HashMap<Integer, NpcMiscInventory> getItemSlots(NBTTagList tagList) {
      HashMap<Integer, NpcMiscInventory> list = new HashMap();

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
         int slot = nbttagcompound.getInteger("Slot");
         NpcMiscInventory inv = new NpcMiscInventory(54);
         inv.setFromNBT(nbttagcompound.getCompoundTag("BankItems"));
         list.put(Integer.valueOf(slot), inv);
      }

      return list;
   }

   public void writeNBT(NBTTagCompound nbttagcompound) {
      nbttagcompound.setInteger("DataBankId", this.bankId);
      nbttagcompound.setInteger("UnlockedSlots", this.unlockedSlots);
      nbttagcompound.setTag("UpdatedSlots", NBTTags.nbtBooleanList(this.upgradedSlots));
      nbttagcompound.setTag("BankInv", this.nbtItemSlots(this.itemSlots));
   }

   private NBTTagList nbtItemSlots(HashMap<Integer, NpcMiscInventory> items) {
      NBTTagList list = new NBTTagList();
      Iterator var3 = items.keySet().iterator();

      while(var3.hasNext()) {
         int slot = ((Integer)var3.next()).intValue();
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setInteger("Slot", slot);
         nbttagcompound.setTag("BankItems", ((NpcMiscInventory)items.get(Integer.valueOf(slot))).getToNBT());
         list.appendTag(nbttagcompound);
      }

      return list;
   }

   public boolean isUpgraded(Bank bank, int slot) {
      if (bank.isUpgraded(slot)) {
         return true;
      } else {
         return bank.canBeUpgraded(slot) && ((Boolean)this.upgradedSlots.get(Integer.valueOf(slot))).booleanValue();
      }
   }

   public void openBankGui(EntityPlayer player, EntityNPCInterface npc, int bankId, int slot) {
      Bank bank = BankController.getInstance().getBank(bankId);
      if (bank.getMaxSlots() > slot) {
         if (bank.startSlots > this.unlockedSlots) {
            this.unlockedSlots = bank.startSlots;
         }

         ItemStack currency = ItemStack.EMPTY;
         if (this.unlockedSlots <= slot) {
            currency = bank.currencyInventory.getStackInSlot(slot);
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerBankUnlock, npc, slot, bank.id, 0);
         } else if (this.isUpgraded(bank, slot)) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerBankLarge, npc, slot, bank.id, 0);
         } else if (bank.canBeUpgraded(slot)) {
            currency = bank.upgradeInventory.getStackInSlot(slot);
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerBankUprade, npc, slot, bank.id, 0);
         } else {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerBankSmall, npc, slot, bank.id, 0);
         }

         CustomNPCsScheduler.runTack(new BankData$1(this, bank, currency, player), 300);
      }
   }

   private ContainerNPCBankInterface getContainer(EntityPlayer player) {
      Container con = player.openContainer;
      return con != null && con instanceof ContainerNPCBankInterface ? (ContainerNPCBankInterface)con : null;
   }

   // $FF: synthetic method
   static ContainerNPCBankInterface access$000(BankData x0, EntityPlayer x1) {
      return x0.getContainer(x1);
   }
}
