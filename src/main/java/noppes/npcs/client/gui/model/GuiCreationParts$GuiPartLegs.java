package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.constants.EnumParts;

class GuiCreationParts$GuiPartLegs extends GuiCreationParts$GuiPart {
   // $FF: synthetic field
   final GuiCreationParts this$0;

   public GuiCreationParts$GuiPartLegs(GuiCreationParts this$0) {
      super(this$0, EnumParts.LEGS);
      this.this$0 = this$0;
      this.types = new String[]{"gui.none", "gui.normal", "legs.naga", "legs.spider", "legs.horse", "legs.mermaid", "legs.digitigrade"};
      this.canBeDeleted = false;
   }

   public int initGui() {
      this.hasPlayerOption = this.data.type == 1 || this.data.type == 5;
      return super.initGui();
   }

   protected void actionPerformed(GuiButton btn) {
      if (btn.id == 20) {
         int i = ((GuiNpcButton)btn).getValue();
         if (i <= 1) {
            this.data.playerTexture = true;
         } else {
            this.data.playerTexture = false;
         }
      }

      super.actionPerformed(btn);
   }
}
