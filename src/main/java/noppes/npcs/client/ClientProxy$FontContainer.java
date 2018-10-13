package noppes.npcs.client;

import java.awt.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.LogWriter;
import noppes.npcs.config.TrueTypeFont;

public class ClientProxy$FontContainer {
   private TrueTypeFont textFont = null;
   public boolean useCustomFont = true;

   private ClientProxy$FontContainer() {
   }

   public ClientProxy$FontContainer(String fontType, int fontSize) {
      this.textFont = new TrueTypeFont(new Font(fontType, 0, fontSize), 1.0F);
      this.useCustomFont = !fontType.equalsIgnoreCase("minecraft");

      try {
         if (!this.useCustomFont || fontType.isEmpty() || fontType.equalsIgnoreCase("default")) {
            this.textFont = new TrueTypeFont(new ResourceLocation("customnpcs", "opensans.ttf"), fontSize, 1.0F);
         }
      } catch (Exception var4) {
         LogWriter.info("Failed loading font so using Arial");
      }

   }

   public int height(String text) {
      return this.useCustomFont ? this.textFont.height(text) : Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
   }

   public int width(String text) {
      return this.useCustomFont ? this.textFont.width(text) : Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
   }

   public ClientProxy$FontContainer copy() {
      ClientProxy$FontContainer font = new ClientProxy$FontContainer();
      font.textFont = this.textFont;
      font.useCustomFont = this.useCustomFont;
      return font;
   }

   public void drawString(String text, int x, int y, int color) {
      if (this.useCustomFont) {
         this.textFont.draw(text, (float)x, (float)y, color);
      } else {
         Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (float)x, (float)y, color);
      }

   }

   public String getName() {
      return !this.useCustomFont ? "Minecraft" : this.textFont.getFontName();
   }

   public void clear() {
      if (this.textFont != null) {
         this.textFont.dispose();
      }

   }
}
