package noppes.npcs.client;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomItems;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.item.IItemStack;

class ClientProxy$2 implements IItemColor {
   // $FF: synthetic field
   final ClientProxy this$0;

   ClientProxy$2(ClientProxy this$0) {
      this.this$0 = this$0;
   }

   public int colorMultiplier(ItemStack stack, int tintIndex) {
      IItemStack item = NpcAPI.Instance().getIItemStack(stack);
      return stack.getItem() == CustomItems.scripted_item ? ((IItemScripted)item).getColor() : -1;
   }
}
