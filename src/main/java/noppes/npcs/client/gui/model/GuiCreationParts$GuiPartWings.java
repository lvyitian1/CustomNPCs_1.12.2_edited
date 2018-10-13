package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.constants.EnumParts;

class GuiCreationParts$GuiPartWings extends GuiCreationParts$GuiPart {
   // $FF: synthetic field
   final GuiCreationParts this$0;

   public GuiCreationParts$GuiPartWings(GuiCreationParts this$0) {
      super(this$0, EnumParts.WINGS);
      this.this$0 = this$0;
      this.setTypes(new String[]{"gui.none", "1", "2", "3", "4"});
   }

   public int initGui() {
      int y = super.initGui();
      return this.data == null ? y : y;
   }

   protected void actionPerformed(GuiButton btn) {
      if (btn.id == 34) {
         ;
      }

      super.actionPerformed(btn);
   }
}
