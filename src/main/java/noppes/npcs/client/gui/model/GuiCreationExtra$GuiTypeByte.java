package noppes.npcs.client.gui.model;

import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiNpcButton;

class GuiCreationExtra$GuiTypeByte extends GuiCreationExtra$GuiType {
   private byte b;
   // $FF: synthetic field
   final GuiCreationExtra this$0;

   public GuiCreationExtra$GuiTypeByte(GuiCreationExtra this$0, String name, byte b) {
      super(this$0, name);
      this.this$0 = this$0;
      this.b = b;
   }

   public void initGui() {
      this.this$0.addButton(new GuiButtonBiDirectional(11, this.this$0.guiLeft + 120, this.this$0.guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}, this.b));
   }

   public void actionPerformed(GuiButton button) {
      if (button.id == 11) {
         this.this$0.playerdata.extra.setByte(this.name, (byte)((GuiNpcButton)button).getValue());
         this.this$0.playerdata.clearEntity();
         GuiCreationExtra.access$000(this.this$0);
      }
   }
}
