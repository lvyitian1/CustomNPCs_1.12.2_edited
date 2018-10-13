package noppes.npcs.client;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.apache.commons.io.FileUtils;

class ImageDownloadAlt$1 extends Thread {
   private static final String __OBFID = "CL_00001050";
   // $FF: synthetic field
   final ImageDownloadAlt this$0;

   ImageDownloadAlt$1(ImageDownloadAlt this$0, String x0) {
      super(x0);
      this.this$0 = this$0;
   }

   public void run() {
      HttpURLConnection connection = null;
      ImageDownloadAlt.access$200().debug("Downloading http texture from {} to {}", new Object[]{ImageDownloadAlt.access$000(this.this$0), ImageDownloadAlt.access$100(this.this$0)});

      try {
         connection = (HttpURLConnection)(new URL(ImageDownloadAlt.access$000(this.this$0))).openConnection(Minecraft.getMinecraft().getProxy());
         connection.setDoInput(true);
         connection.setDoOutput(false);
         connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
         connection.connect();
         if (connection.getResponseCode() / 100 == 2) {
            BufferedImage bufferedimage;
            if (ImageDownloadAlt.access$100(this.this$0) != null) {
               FileUtils.copyInputStreamToFile(connection.getInputStream(), ImageDownloadAlt.access$100(this.this$0));
               bufferedimage = ImageIO.read(ImageDownloadAlt.access$100(this.this$0));
            } else {
               bufferedimage = TextureUtil.readBufferedImage(connection.getInputStream());
            }

            if (ImageDownloadAlt.access$300(this.this$0) != null) {
               bufferedimage = ImageDownloadAlt.access$300(this.this$0).parseUserSkin(bufferedimage);
            }

            this.this$0.setBufferedImage(bufferedimage);
            return;
         }
      } catch (Exception var6) {
         ImageDownloadAlt.access$200().error("Couldn't download http texture", var6);
         return;
      } finally {
         if (connection != null) {
            connection.disconnect();
         }

      }

   }
}
