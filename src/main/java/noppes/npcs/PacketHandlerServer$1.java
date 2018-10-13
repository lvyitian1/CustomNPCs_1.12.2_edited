package noppes.npcs;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;

class PacketHandlerServer$1 implements Runnable {
   // $FF: synthetic field
   final ByteBuf val$buffer;
   // $FF: synthetic field
   final EntityPlayerMP val$player;
   // $FF: synthetic field
   final PacketHandlerServer this$0;

   PacketHandlerServer$1(PacketHandlerServer this$0, ByteBuf var2, EntityPlayerMP var3) {
      this.this$0 = this$0;
      this.val$buffer = var2;
      this.val$player = var3;
   }

   public void run() {
      EnumPacketServer type = null;

      try {
         type = EnumPacketServer.values()[this.val$buffer.readInt()];
         ItemStack item = this.val$player.inventory.getCurrentItem();
         EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(this.val$player);
         if (!type.needsNpc || npc != null) {
            if (type.hasPermission()) {
               CustomNpcsPermissions var10000 = CustomNpcsPermissions.Instance;
               if (!CustomNpcsPermissions.hasPermission(this.val$player, type.permission)) {
                  return;
               }
            }

            if (!type.isExempt() && !PacketHandlerServer.access$000(this.this$0, item, type)) {
               PacketHandlerServer.access$100(this.this$0, this.val$player, "tried to use custom npcs without a tool in hand, possibly a hacker");
            } else {
               PacketHandlerServer.access$200(this.this$0, type, this.val$buffer, this.val$player, npc);
            }
         }
      } catch (Exception var4) {
         LogWriter.error("Error with EnumPacketServer." + type, var4);
      }

   }
}
