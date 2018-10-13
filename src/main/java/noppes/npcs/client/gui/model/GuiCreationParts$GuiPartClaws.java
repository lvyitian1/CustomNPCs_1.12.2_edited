package noppes.npcs.client.gui.model;

import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.constants.EnumParts;

class GuiCreationParts$GuiPartClaws extends GuiCreationParts$GuiPart {
   // $FF: synthetic field
   final GuiCreationParts this$0;

   public GuiCreationParts$GuiPartClaws(GuiCreationParts this$0) {
      super(this$0, EnumParts.CLAWS);
      this.this$0 = this$0;
      this.types = new String[]{"gui.none", "gui.show"};
   }

   public int initGui() {
      int y = super.initGui();
      if (this.data == null) {
         return y;
      } else {
         this.this$0.addLabel(new GuiNpcLabel(22, "gui.pattern", this.this$0.guiLeft + 102, y + 5, 16777215));
         this.this$0.addButton(new GuiButtonBiDirectional(22, this.this$0.guiLeft + 145, y, 100, 20, new String[]{"gui.both", "gui.left", "gui.right"}, this.data.pattern));
         return y;
      }
   }
}
