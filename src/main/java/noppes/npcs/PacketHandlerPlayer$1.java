package noppes.npcs;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.constants.EnumPlayerPacket;

class PacketHandlerPlayer$1 implements Runnable {
   // $FF: synthetic field
   final ByteBuf val$buffer;
   // $FF: synthetic field
   final EntityPlayerMP val$player;
   // $FF: synthetic field
   final PacketHandlerPlayer this$0;

   PacketHandlerPlayer$1(PacketHandlerPlayer this$0, ByteBuf var2, EntityPlayerMP var3) {
      this.this$0 = this$0;
      this.val$buffer = var2;
      this.val$player = var3;
   }

   public void run() {
      EnumPlayerPacket type = null;

      try {
         type = EnumPlayerPacket.values()[this.val$buffer.readInt()];
         PacketHandlerPlayer.access$000(this.this$0, this.val$buffer, this.val$player, type);
      } catch (Exception var3) {
         LogWriter.error("Error with EnumPlayerPacket." + type, var3);
      }

   }
}
