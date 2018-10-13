package micdoodle8.mods.galacticraft.api.client.tabs;

import net.minecraft.client.Minecraft;
import noppes.npcs.client.gui.player.GuiFaction;

class InventoryTabFactions$1 implements Runnable {
   // $FF: synthetic field
   final InventoryTabFactions this$0;

   InventoryTabFactions$1(InventoryTabFactions this$0) {
      this.this$0 = this$0;
   }

   public void run() {
      Minecraft mc = Minecraft.getMinecraft();
      mc.displayGuiScreen(new GuiFaction());
   }
}
