package noppes.npcs;

import com.sun.corba.se.spi.ior.ObjectKey;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.npcs.constants.EnumPacketClient;

final class Server$2 implements Runnable {
   // $FF: synthetic field
   final EnumPacketClient val$enu;
   // $FF: synthetic field
   final Object[] val$obs;
   // $FF: synthetic field
   final List val$list;

   Server$2(EnumPacketClient var1, Object[] var2, List var3) {
      this.val$enu = var1;
      this.val$obs = var2;
      this.val$list = var3;
   }

   public void run() {
      ByteBuf buffer = Unpooled.buffer();

      try {
         if (!Server.fillBuffer(buffer, this.val$enu, this.val$obs)) {
            return;
         }

         for(Object player : this.val$list) {
            CustomNpcs.Channel.sendTo(new FMLProxyPacket(new PacketBuffer(buffer.copy()), "CustomNPCs"), (EntityPlayerMP)player);
         }
      } catch (IOException var4) {
         LogWriter.error(this.val$enu + " Errored", var4);
      }

   }
}
