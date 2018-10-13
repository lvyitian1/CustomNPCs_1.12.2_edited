package noppes.npcs.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ImageDownloadAlt extends SimpleTexture {
   private static final Logger logger = LogManager.getLogger();
   private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
   private final File cacheFile;
   private final String imageUrl;
   private final IImageBuffer imageBuffer;
   private BufferedImage bufferedImage;
   private Thread imageThread;
   private boolean textureUploaded;

   public ImageDownloadAlt(File file, String url, ResourceLocation resource, IImageBuffer buffer) {
      super(resource);
      this.cacheFile = file;
      this.imageUrl = url;
      this.imageBuffer = buffer;
   }

   private void checkTextureUploaded() {
      if (!this.textureUploaded && this.bufferedImage != null) {
         if (this.textureLocation != null) {
            this.deleteGlTexture();
         }

         TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
         this.textureUploaded = true;
      }

   }

   public int getGlTextureId() {
      this.checkTextureUploaded();
      return super.getGlTextureId();
   }

   public void setBufferedImage(BufferedImage p_147641_1_) {
      this.bufferedImage = p_147641_1_;
      if (this.imageBuffer != null) {
         this.imageBuffer.skinAvailable();
      }

   }

   public void loadTexture(IResourceManager resourceManager) throws IOException {
      if (this.bufferedImage == null && this.textureLocation != null) {
         super.loadTexture(resourceManager);
      }

      if (this.imageThread == null) {
         if (this.cacheFile != null && this.cacheFile.isFile()) {
            logger.debug("Loading http texture from local cache ({})", new Object[]{this.cacheFile});

            try {
               this.bufferedImage = ImageIO.read(this.cacheFile);
               if (this.imageBuffer != null) {
                  this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
               }
            } catch (IOException var3) {
               logger.error("Couldn't load skin " + this.cacheFile, var3);
               this.loadTextureFromServer();
            }
         } else {
            this.loadTextureFromServer();
         }
      }

   }

   protected void loadTextureFromServer() {
      this.imageThread = new ImageDownloadAlt$1(this, "Texture Downloader #" + threadDownloadCounter.incrementAndGet());
      this.imageThread.setDaemon(true);
      this.imageThread.start();
   }

   // $FF: synthetic method
   static String access$000(ImageDownloadAlt x0) {
      return x0.imageUrl;
   }

   // $FF: synthetic method
   static File access$100(ImageDownloadAlt x0) {
      return x0.cacheFile;
   }

   // $FF: synthetic method
   static Logger access$200() {
      return logger;
   }

   // $FF: synthetic method
   static IImageBuffer access$300(ImageDownloadAlt x0) {
      return x0.imageBuffer;
   }
}
