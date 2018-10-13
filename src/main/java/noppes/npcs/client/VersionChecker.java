package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;

public class VersionChecker extends Thread {
   public void run() {
      String name = "\u00a72CustomNpcs\u00a7f";
      String link = "\u00a79\u00a7nClick here";
      String text = name + " installed. For more info " + link;

      try {
         EntityPlayer player = Minecraft.getMinecraft().player;
      } catch (NoSuchMethodError var7) {
         return;
      }

      EntityPlayerSP var8;
      while((var8 = Minecraft.getMinecraft().player) == null) {
         try {
            Thread.sleep(2000L);
         } catch (InterruptedException var6) {
            var6.printStackTrace();
         }
      }

      TextComponentTranslation message = new TextComponentTranslation(text, new Object[0]);
      message.getStyle().setClickEvent(new ClickEvent(Action.OPEN_URL, "http://www.kodevelopment.nl/minecraft/customnpcs/"));
      var8.sendMessage(message);
   }
}
