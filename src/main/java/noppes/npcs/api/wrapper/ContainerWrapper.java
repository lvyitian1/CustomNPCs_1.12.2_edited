package noppes.npcs.api.wrapper;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;

public class ContainerWrapper implements IContainer {
   private IInventory inventory;
   private Container container;

   public ContainerWrapper(IInventory inventory) {
      this.inventory = inventory;
   }

   public ContainerWrapper(Container container) {
      this.container = container;
   }

   public int getSize() {
      return this.inventory != null ? this.inventory.getSizeInventory() : this.container.inventorySlots.size();
   }

   public IItemStack getSlot(int slot) {
      if (slot >= 0 && slot < this.getSize()) {
         return this.inventory != null ? NpcAPI.Instance().getIItemStack(this.inventory.getStackInSlot(slot)) : NpcAPI.Instance().getIItemStack(this.container.getSlot(slot).getStack());
      } else {
         throw new CustomNPCsException("Slot is out of range " + slot, new Object[0]);
      }
   }

   public void setSlot(int slot, IItemStack item) {
      if (slot >= 0 && slot < this.getSize()) {
         ItemStack itemstack = item == null ? ItemStack.EMPTY : item.getMCItemStack();
         if (this.inventory != null) {
            this.inventory.setInventorySlotContents(slot, itemstack);
         } else {
            this.container.putStackInSlot(slot, itemstack);
         }

      } else {
         throw new CustomNPCsException("Slot is out of range " + slot, new Object[0]);
      }
   }

   public IInventory getMCInventory() {
      return this.inventory;
   }

   public Container getMCContainer() {
      return this.container;
   }
}
