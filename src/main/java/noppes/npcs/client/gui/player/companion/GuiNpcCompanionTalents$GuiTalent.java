package noppes.npcs.client.gui.player.companion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.roles.RoleCompanion;

public class GuiNpcCompanionTalents$GuiTalent extends GuiScreen {
   private EnumCompanionTalent talent;
   private int x;
   private int y;
   private RoleCompanion role;
   private static final ResourceLocation resource = new ResourceLocation("customnpcs:textures/gui/talent.png");

   public GuiNpcCompanionTalents$GuiTalent(RoleCompanion role, EnumCompanionTalent talent, int x, int y) {
      this.talent = talent;
      this.x = x;
      this.y = y;
      this.role = role;
   }

   public void drawScreen(int i, int j, float f) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.getTextureManager().bindTexture(resource);
      ItemStack item = this.talent.item;
      if (item.getItem() == null) {
         item = new ItemStack(Blocks.DIRT);
      }

      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      boolean hover = this.x < i && this.x + 24 > i && this.y < j && this.y + 24 > j;
      this.drawTexturedModalRect(this.x, this.y, 0, hover ? 24 : 0, 24, 24);
      this.zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      GlStateManager.enableLighting();
      GlStateManager.enableRescaleNormal();
      RenderHelper.enableGUIStandardItemLighting();
      this.itemRender.renderItemAndEffectIntoGUI(item, this.x + 4, this.y + 4);
      this.itemRender.renderItemOverlays(mc.fontRenderer, item, this.x + 4, this.y + 4);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.translate(0.0F, 0.0F, 200.0F);
      this.drawCenteredString(mc.fontRenderer, this.role.getTalentLevel(this.talent) + "", this.x + 20, this.y + 16, 16777215);
      this.itemRender.zLevel = 0.0F;
      this.zLevel = 0.0F;
      GlStateManager.popMatrix();
   }

   // $FF: synthetic method
   static EnumCompanionTalent access$000(GuiNpcCompanionTalents$GuiTalent x0) {
      return x0.talent;
   }
}
