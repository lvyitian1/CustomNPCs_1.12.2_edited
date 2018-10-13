package micdoodle8.mods.galacticraft.api.client.tabs;

import net.minecraft.client.Minecraft;
import noppes.npcs.client.gui.player.GuiQuestLog;

class InventoryTabQuests$1 implements Runnable {
   // $FF: synthetic field
   final InventoryTabQuests this$0;

   InventoryTabQuests$1(InventoryTabQuests this$0) {
      this.this$0 = this$0;
   }

   public void run() {
      Minecraft mc = Minecraft.getMinecraft();
      mc.displayGuiScreen(new GuiQuestLog(mc.player));
   }
}
