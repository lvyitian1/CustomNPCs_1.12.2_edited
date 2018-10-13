package noppes.npcs.client.gui.model;

import noppes.npcs.constants.EnumParts;

class GuiCreationParts$GuiPartParticles extends GuiCreationParts$GuiPart {
   // $FF: synthetic field
   final GuiCreationParts this$0;

   public GuiCreationParts$GuiPartParticles(GuiCreationParts this$0) {
      super(this$0, EnumParts.PARTICLES);
      this.this$0 = this$0;
      this.types = new String[]{"gui.none", "1", "2"};
   }

   public int initGui() {
      int y = super.initGui();
      return this.data == null ? y : y;
   }
}
