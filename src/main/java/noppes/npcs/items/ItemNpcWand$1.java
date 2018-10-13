package noppes.npcs.items;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityCustomNpc;

class ItemNpcWand$1 implements Runnable {
   // $FF: synthetic field
   final EntityPlayer val$player;
   // $FF: synthetic field
   final EntityCustomNpc val$npc;
   // $FF: synthetic field
   final ItemNpcWand this$0;

   ItemNpcWand$1(ItemNpcWand this$0, EntityPlayer var2, EntityCustomNpc var3) {
      this.this$0 = this$0;
      this.val$player = var2;
      this.val$npc = var3;
   }

   public void run() {
      NoppesUtilServer.sendOpenGui(this.val$player, EnumGuiType.MainMenuDisplay, this.val$npc);
   }
}
