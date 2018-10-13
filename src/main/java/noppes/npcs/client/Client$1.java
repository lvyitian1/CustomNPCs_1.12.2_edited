package noppes.npcs.client;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.npcs.CustomNpcs;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketServer;

final class Client$1 implements Runnable {
   // $FF: synthetic field
   final EnumPacketServer val$enu;
   // $FF: synthetic field
   final Object[] val$obs;

   Client$1(EnumPacketServer var1, Object[] var2) {
      this.val$enu = var1;
      this.val$obs = var2;
   }

   public void run() {
      PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

      try {
         if (!Server.fillBuffer(buffer, this.val$enu, this.val$obs)) {
            return;
         }

         CustomNpcs.Channel.sendToServer(new FMLProxyPacket(buffer, "CustomNPCs"));
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }
}
