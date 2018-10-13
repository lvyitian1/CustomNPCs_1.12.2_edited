package noppes.npcs.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.CustomNpcs;

class PacketHandlerClient$2 implements Runnable {
   // $FF: synthetic field
   final String val$font;
   // $FF: synthetic field
   final int val$size;
   // $FF: synthetic field
   final EntityPlayer val$player;
   // $FF: synthetic field
   final PacketHandlerClient this$0;

   PacketHandlerClient$2(PacketHandlerClient this$0, String var2, int var3, EntityPlayer var4) {
      this.this$0 = this$0;
      this.val$font = var2;
      this.val$size = var3;
      this.val$player = var4;
   }

   public void run() {
      if (!this.val$font.isEmpty()) {
         CustomNpcs.FontType = this.val$font;
         CustomNpcs.FontSize = this.val$size;
         ClientProxy.Font.clear();
         ClientProxy.Font = new ClientProxy$FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
         CustomNpcs.Config.updateConfig();
         this.val$player.sendMessage(new TextComponentTranslation("Font set to %s", new Object[]{ClientProxy.Font.getName()}));
      } else {
         this.val$player.sendMessage(new TextComponentTranslation("Current font is %s", new Object[]{ClientProxy.Font.getName()}));
      }

   }
}
