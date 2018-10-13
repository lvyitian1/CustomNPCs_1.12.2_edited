package noppes.npcs.config;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.GlStateManager;

class TrueTypeFont$TextureCache {
   int x;
   int y;
   int textureId;
   BufferedImage bufferedImage;
   Graphics2D g;
   boolean full;
   // $FF: synthetic field
   final TrueTypeFont this$0;

   TrueTypeFont$TextureCache(TrueTypeFont this$0) {
      this.this$0 = this$0;
      this.textureId = GlStateManager.generateTexture();
      this.bufferedImage = new BufferedImage(512, 512, 2);
      this.g = (Graphics2D)this.bufferedImage.getGraphics();
   }
}
