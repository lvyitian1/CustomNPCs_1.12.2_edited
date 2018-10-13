package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;

public class ContainerMail extends ContainerNpcInterface {
   private static final String __OBFID = "CL_00001742";
   public static PlayerMail staticmail = new PlayerMail();
   public PlayerMail mail = new PlayerMail();
   private boolean canEdit;
   private boolean canSend;

   public ContainerMail(EntityPlayer player, boolean canEdit, boolean canSend) {
      super(player);
      this.mail = staticmail;
      staticmail = new PlayerMail();
      this.canEdit = canEdit;
      this.canSend = canSend;
      player.inventory.openInventory(player);

      for(int k = 0; k < 4; ++k) {
         this.addSlotToContainer(new SlotValid(this.mail, k, 179 + k * 24, 138, canEdit));
      }

      for(int j = 0; j < 3; ++j) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot(player.inventory, var6 + j * 9 + 9, 28 + var6 * 18, 175 + j * 18));
         }
      }

      for(int j = 0; j < 9; ++j) {
         this.addSlotToContainer(new Slot(player.inventory, j, 28 + j * 18, 230));
      }

   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(par2);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (par2 < 4) {
            if (!this.mergeItemStack(itemstack1, 4, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.canEdit || !this.mergeItemStack(itemstack1, 0, 4, false)) {
            return null;
         }

         if (itemstack1.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }

   public void onContainerClosed(EntityPlayer player) {
      super.onContainerClosed(player);
      if (!this.canEdit && !player.world.isRemote) {
         PlayerMailData data = PlayerData.get(player).mailData;

         for(PlayerMail mail : data.playermail) {
            if (mail.time == this.mail.time && mail.sender.equals(this.mail.sender)) {
               mail.readNBT(this.mail.writeNBT());
               break;
            }
         }
      }

   }
}
