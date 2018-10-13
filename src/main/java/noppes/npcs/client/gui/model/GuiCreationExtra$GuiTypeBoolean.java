package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;

class GuiCreationExtra$GuiTypeBoolean extends GuiCreationExtra$GuiType {
   private boolean bo;
   // $FF: synthetic field
   final GuiCreationExtra this$0;

   public GuiCreationExtra$GuiTypeBoolean(GuiCreationExtra this$0, String name, boolean bo) {
      super(this$0, name);
      this.this$0 = this$0;
      this.bo = bo;
   }

   public void initGui() {
      this.this$0.addButton(new GuiNpcButtonYesNo(11, this.this$0.guiLeft + 120, this.this$0.guiTop + 50, 60, 20, this.bo));
   }

   public void actionPerformed(GuiButton button) {
      if (button.id == 11) {
         this.bo = ((GuiNpcButtonYesNo)button).getBoolean();
         if (this.name.equals("Child")) {
            this.this$0.playerdata.extra.setInteger("Age", this.bo ? -24000 : 0);
            this.this$0.playerdata.clearEntity();
         } else {
            this.this$0.playerdata.extra.setBoolean(this.name, this.bo);
            this.this$0.playerdata.clearEntity();
            GuiCreationExtra.access$000(this.this$0);
         }

      }
   }
}
