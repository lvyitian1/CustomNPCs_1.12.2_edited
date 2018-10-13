package noppes.npcs.client;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

class ClientProxy$1 implements IItemColor {
   // $FF: synthetic field
   final ClientProxy this$0;

   ClientProxy$1(ClientProxy this$0) {
      this.this$0 = this$0;
   }

   public int colorMultiplier(ItemStack stack, int tintIndex) {
      return 9127187;
   }
}
