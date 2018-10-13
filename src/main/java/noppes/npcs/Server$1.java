package noppes.npcs;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.npcs.constants.EnumPacketClient;

final class Server$1 implements Runnable {
   // $FF: synthetic field
   final EnumPacketClient val$enu;
   // $FF: synthetic field
   final Object[] val$obs;
   // $FF: synthetic field
   final EntityPlayerMP val$player;

   Server$1(EnumPacketClient var1, Object[] var2, EntityPlayerMP var3) {
      this.val$enu = var1;
      this.val$obs = var2;
      this.val$player = var3;
   }

   public void run() {
      PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

      try {
         if (!Server.fillBuffer(buffer, this.val$enu, this.val$obs)) {
            return;
         }

         CustomNpcs.Channel.sendTo(new FMLProxyPacket(buffer, "CustomNPCs"), this.val$player);
      } catch (IOException var3) {
         LogWriter.error(this.val$enu + " Errored", var3);
      }

   }
}
