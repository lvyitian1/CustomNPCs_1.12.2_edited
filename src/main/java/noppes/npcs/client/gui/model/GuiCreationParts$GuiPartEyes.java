package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.ModelEyeData;
import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiColorButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.constants.EnumParts;

class GuiCreationParts$GuiPartEyes extends GuiCreationParts$GuiPart {
   private ModelEyeData eyes;
   // $FF: synthetic field
   final GuiCreationParts this$0;

   public GuiCreationParts$GuiPartEyes(GuiCreationParts this$0) {
      super(this$0, EnumParts.EYES);
      this.this$0 = this$0;
      this.types = new String[]{"gui.none", "1", "2"};
      this.noPlayerOptions();
      this.canBeDeleted = false;
      this.eyes = (ModelEyeData)this.data;
   }

   public int initGui() {
      int y = super.initGui();
      if (this.data != null && this.eyes.isEnabled()) {
         this.this$0.addButton(new GuiButtonBiDirectional(22, this.this$0.guiLeft + 145, y, 100, 20, new String[]{"gui.both", "gui.left", "gui.right"}, this.data.pattern));
         this.this$0.addLabel(new GuiNpcLabel(22, "gui.draw", this.this$0.guiLeft + 102, y + 5, 16777215));
         int var10004 = this.this$0.guiLeft + 145;
         int var2 = y + 25;
         this.this$0.addButton(new GuiButtonBiDirectional(37, var10004, var2, 100, 20, new String[]{I18n.translateToLocal("gui.down") + "x2", "gui.down", "gui.normal", "gui.up"}, this.eyes.eyePos + 1));
         this.this$0.addLabel(new GuiNpcLabel(37, "gui.position", this.this$0.guiLeft + 102, var2 + 5, 16777215));
         var10004 = this.this$0.guiLeft + 145;
         int var3 = var2 + 25;
         this.this$0.addButton(new GuiNpcButtonYesNo(34, var10004, var3, this.eyes.glint));
         this.this$0.addLabel(new GuiNpcLabel(34, "eye.glint", this.this$0.guiLeft + 102, var3 + 5, 16777215));
         var10004 = this.this$0.guiLeft + 170;
         int var4 = var3 + 25;
         this.this$0.addButton(new GuiColorButton(35, var10004, var4, this.eyes.browColor));
         this.this$0.addLabel(new GuiNpcLabel(35, "eye.brow", this.this$0.guiLeft + 102, var4 + 5, 16777215));
         this.this$0.addButton(new GuiButtonBiDirectional(38, this.this$0.guiLeft + 225, var4, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"}, this.eyes.browThickness));
         var10004 = this.this$0.guiLeft + 170;
         y = var4 + 25;
         this.this$0.addButton(new GuiColorButton(36, var10004, y, this.eyes.skinColor));
         this.this$0.addLabel(new GuiNpcLabel(36, "eye.lid", this.this$0.guiLeft + 102, y + 5, 16777215));
      }

      return y;
   }

   protected void actionPerformed(GuiButton btn) {
      if (btn.id == 34) {
         this.eyes.glint = ((GuiNpcButtonYesNo)btn).getBoolean();
      }

      if (btn.id == 35) {
         this.this$0.setSubGui(new GuiModelColor(this.this$0, this.eyes.browColor, new GuiCreationParts$GuiPartEyes$1(this)));
      }

      if (btn.id == 36) {
         this.this$0.setSubGui(new GuiModelColor(this.this$0, this.eyes.skinColor, new GuiCreationParts$GuiPartEyes$2(this)));
      }

      if (btn.id == 37) {
         this.eyes.eyePos = ((GuiButtonBiDirectional)btn).getValue() - 1;
      }

      if (btn.id == 38) {
         this.eyes.browThickness = ((GuiButtonBiDirectional)btn).getValue();
      }

      super.actionPerformed(btn);
   }

   // $FF: synthetic method
   static ModelEyeData access$000(GuiCreationParts$GuiPartEyes x0) {
      return x0.eyes;
   }
}
