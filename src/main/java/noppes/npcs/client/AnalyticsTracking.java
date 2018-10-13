package noppes.npcs.client;

import net.minecraft.entity.player.EntityPlayer;

public class AnalyticsTracking {
   public static void sendData(EntityPlayer player, String event, String data) {
      (new Thread(new AnalyticsTracking$1(player, event, data))).start();
   }
}
