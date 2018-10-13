package noppes.npcs.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class GuiMerchantAdd$MerchantButton extends GuiButton {
   private final boolean forward;
   private static final String __OBFID = "CL_00000763";

   public GuiMerchantAdd$MerchantButton(int par1, int par2, int par3, boolean par4) {
      super(par1, par2, par3, 12, 19, "");
      this.forward = par4;
   }

   public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_, float partialTicks) {
      if (this.visible) {
         p_146112_1_.getTextureManager().bindTexture(GuiMerchantAdd.access$000());
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         boolean flag = p_146112_2_ >= this.x && p_146112_3_ >= this.y && p_146112_2_ < this.x + this.width && p_146112_3_ < this.y + this.height;
         int k = 0;
         int l = 176;
         if (!this.enabled) {
            l += this.width * 2;
         } else if (flag) {
            l += this.width;
         }

         if (!this.forward) {
            k += this.height;
         }

         this.drawTexturedModalRect(this.x, this.y, l, k, this.width, this.height);
      }

   }
}
