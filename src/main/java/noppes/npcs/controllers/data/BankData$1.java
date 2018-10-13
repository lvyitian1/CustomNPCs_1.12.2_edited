package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.containers.ContainerNPCBankInterface;

class BankData$1 implements Runnable {
   // $FF: synthetic field
   final Bank val$bank;
   // $FF: synthetic field
   final ItemStack val$item;
   // $FF: synthetic field
   final EntityPlayer val$player;
   // $FF: synthetic field
   final BankData this$0;

   BankData$1(BankData this$0, Bank var2, ItemStack var3, EntityPlayer var4) {
      this.this$0 = this$0;
      this.val$bank = var2;
      this.val$item = var3;
      this.val$player = var4;
   }

   public void run() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setInteger("MaxSlots", this.val$bank.getMaxSlots());
      compound.setInteger("UnlockedSlots", this.this$0.unlockedSlots);
      if (this.val$item != null && !this.val$item.isEmpty()) {
         compound.setTag("Currency", this.val$item.writeToNBT(new NBTTagCompound()));
         ContainerNPCBankInterface container = BankData.access$000(this.this$0, this.val$player);
         if (container != null) {
            container.setCurrency(this.val$item);
         }
      }

      Server.sendDataChecked((EntityPlayerMP)this.val$player, EnumPacketClient.GUI_DATA, compound);
   }
}
