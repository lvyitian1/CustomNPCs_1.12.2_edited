package noppes.npcs;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.entity.EntityNPCInterface;

final class NoppesUtilServer$2 implements Runnable {
   // $FF: synthetic field
   final EnumGuiType val$gui;
   // $FF: synthetic field
   final EntityPlayer val$player;
   // $FF: synthetic field
   final int val$i;
   // $FF: synthetic field
   final int val$j;
   // $FF: synthetic field
   final int val$k;
   // $FF: synthetic field
   final EntityNPCInterface val$npc;

   NoppesUtilServer$2(EnumGuiType var1, EntityPlayer var2, int var3, int var4, int var5, EntityNPCInterface var6) {
      this.val$gui = var1;
      this.val$player = var2;
      this.val$i = var3;
      this.val$j = var4;
      this.val$k = var5;
      this.val$npc = var6;
   }

   public void run() {
      if (CustomNpcs.proxy.getServerGuiElement(this.val$gui.ordinal(), this.val$player, this.val$player.world, this.val$i, this.val$j, this.val$k) != null) {
         this.val$player.openGui(CustomNpcs.instance, this.val$gui.ordinal(), this.val$player.world, this.val$i, this.val$j, this.val$k);
      } else {
         Server.sendDataChecked((EntityPlayerMP)this.val$player, EnumPacketClient.GUI, this.val$gui.ordinal(), this.val$i, this.val$j, this.val$k);
         ArrayList<String> list = NoppesUtilServer.access$000(this.val$player, this.val$gui, this.val$npc);
         if (list != null && !list.isEmpty()) {
            Server.sendData((EntityPlayerMP)this.val$player, EnumPacketClient.SCROLL_LIST, list);
         }
      }
   }
}
