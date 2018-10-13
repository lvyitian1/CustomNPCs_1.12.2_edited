package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.ModelPartData;
import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiColorButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.constants.EnumParts;

class GuiCreationParts$GuiPart {
   EnumParts part;
   private int paterns;
   protected String[] types;
   protected ModelPartData data;
   protected boolean hasPlayerOption;
   protected boolean noPlayerTypes;
   protected boolean canBeDeleted;
   // $FF: synthetic field
   final GuiCreationParts this$0;

   public GuiCreationParts$GuiPart(GuiCreationParts this$0, EnumParts part) {
      this.this$0 = this$0;
      this.paterns = 0;
      this.types = new String[]{"gui.none"};
      this.hasPlayerOption = true;
      this.noPlayerTypes = false;
      this.canBeDeleted = true;
      this.part = part;
      this.data = this$0.playerdata.getPartData(part);
   }

   public int initGui() {
      this.data = this.this$0.playerdata.getPartData(this.part);
      int y = this.this$0.guiTop + 50;
      if (this.data == null || !this.data.playerTexture || !this.noPlayerTypes) {
         this.this$0.addLabel(new GuiNpcLabel(20, "gui.type", this.this$0.guiLeft + 102, y + 5, 16777215));
         this.this$0.addButton(new GuiButtonBiDirectional(20, this.this$0.guiLeft + 145, y, 100, 20, this.types, this.data == null ? 0 : this.data.type + 1));
         y += 25;
      }

      if (this.data != null && this.hasPlayerOption) {
         this.this$0.addLabel(new GuiNpcLabel(21, "gui.playerskin", this.this$0.guiLeft + 102, y + 5, 16777215));
         this.this$0.addButton(new GuiNpcButtonYesNo(21, this.this$0.guiLeft + 170, y, this.data.playerTexture));
         y += 25;
      }

      if (this.data != null && !this.data.playerTexture) {
         this.this$0.addLabel(new GuiNpcLabel(23, "gui.color", this.this$0.guiLeft + 102, y + 5, 16777215));
         this.this$0.addButton(new GuiColorButton(23, this.this$0.guiLeft + 170, y, this.data.color));
         y += 25;
      }

      return y;
   }

   protected void actionPerformed(GuiButton btn) {
      if (btn.id == 20) {
         int i = ((GuiNpcButton)btn).getValue();
         if (i == 0 && this.canBeDeleted) {
            this.this$0.playerdata.removePart(this.part);
         } else {
            this.data = this.this$0.playerdata.getOrCreatePart(this.part);
            this.data.pattern = 0;
            this.data.setType(i - 1);
         }

         this.this$0.initGui();
      }

      if (btn.id == 22) {
         this.data.pattern = (byte)((GuiNpcButton)btn).getValue();
      }

      if (btn.id == 21) {
         this.data.playerTexture = ((GuiNpcButtonYesNo)btn).getBoolean();
         this.this$0.initGui();
      }

      if (btn.id == 23) {
         this.this$0.setSubGui(new GuiModelColor(this.this$0, this.data.color, new GuiCreationParts$GuiPart$1(this)));
      }

   }

   public GuiCreationParts$GuiPart noPlayerOptions() {
      this.hasPlayerOption = false;
      return this;
   }

   public GuiCreationParts$GuiPart noPlayerTypes() {
      this.noPlayerTypes = true;
      return this;
   }

   public GuiCreationParts$GuiPart setTypes(String[] types) {
      this.types = types;
      return this;
   }
}
