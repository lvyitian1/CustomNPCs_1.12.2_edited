package noppes.npcs.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.LogWriter;

final class AnalyticsTracking$1 implements Runnable {
   // $FF: synthetic field
   final EntityPlayer val$player;
   // $FF: synthetic field
   final String val$event;
   // $FF: synthetic field
   final String val$data;

   AnalyticsTracking$1(EntityPlayer var1, String var2, String var3) {
      this.val$player = var1;
      this.val$event = var2;
      this.val$data = var3;
   }

   public void run() {
      try {
         UUID uuid = this.val$player.getUniqueID();
         String analyticsPostData = "v=1&tid=UA-29079943-5&cid=" + uuid.toString() + "&t=event&ec=customnpcs_1.12&ea=" + this.val$event + "&el=" + this.val$data + "&ev=300";
         URL url = new URL("http://www.google-analytics.com/collect");
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setConnectTimeout(10000);
         connection.setReadTimeout(10000);
         connection.setDoOutput(true);
         connection.setUseCaches(false);
         connection.setRequestMethod("POST");
         connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         connection.setRequestProperty("Content-Length", Integer.toString(analyticsPostData.getBytes().length));
         OutputStream dataOutput = connection.getOutputStream();
         dataOutput.write(analyticsPostData.getBytes());
         dataOutput.close();
         connection.getInputStream().close();
      } catch (IOException var6) {
         LogWriter.except(var6);
      }

   }
}
