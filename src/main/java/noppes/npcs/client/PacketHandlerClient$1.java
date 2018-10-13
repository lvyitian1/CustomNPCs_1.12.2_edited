package noppes.npcs.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.LogWriter;
import noppes.npcs.constants.EnumPacketClient;

class PacketHandlerClient$1 implements Runnable {
   // $FF: synthetic field
   final ByteBuf val$buffer;
   // $FF: synthetic field
   final EntityPlayer val$player;
   // $FF: synthetic field
   final PacketHandlerClient this$0;

   PacketHandlerClient$1(PacketHandlerClient this$0, ByteBuf var2, EntityPlayer var3) {
      this.this$0 = this$0;
      this.val$buffer = var2;
      this.val$player = var3;
   }

   public void run() {
      EnumPacketClient en = null;

      try {
         en = EnumPacketClient.values()[this.val$buffer.readInt()];
         PacketHandlerClient.access$000(this.this$0, this.val$buffer, this.val$player, en);
      } catch (Exception var3) {
         LogWriter.error("Error with EnumPacketClient." + en, var3);
      }

   }
}
