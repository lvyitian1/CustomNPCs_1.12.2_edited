package noppes.npcs.client.gui.model;

import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.constants.EnumParts;

class GuiCreationParts$GuiPartTail extends GuiCreationParts$GuiPart {
   // $FF: synthetic field
   final GuiCreationParts this$0;

   public GuiCreationParts$GuiPartTail(GuiCreationParts this$0) {
      super(this$0, EnumParts.TAIL);
      this.this$0 = this$0;
      this.types = new String[]{"gui.none", "part.tail", "tail.dragon", "tail.horse", "tail.squirrel", "tail.fin", "tail.rodent", "tail.bird", "tail.fox"};
   }

   public int initGui() {
      this.data = this.this$0.playerdata.getPartData(this.part);
      this.hasPlayerOption = this.data != null && (this.data.type == 0 || this.data.type == 1 || this.data.type == 6 || this.data.type == 7);
      int y = super.initGui();
      if (this.data != null && this.data.type == 0) {
         this.this$0.addLabel(new GuiNpcLabel(22, "gui.pattern", this.this$0.guiLeft + 102, y + 5, 16777215));
         this.this$0.addButton(new GuiButtonBiDirectional(22, this.this$0.guiLeft + 145, y, 100, 20, new String[]{"1", "2"}, this.data.pattern));
      }

      return y;
   }
}
