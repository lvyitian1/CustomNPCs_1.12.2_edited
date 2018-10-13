package noppes.npcs.client.gui.model;

import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.controllers.PixelmonHelper;

class GuiCreationExtra$GuiTypePixelmon extends GuiCreationExtra$GuiType {
   // $FF: synthetic field
   final GuiCreationExtra this$0;

   public GuiCreationExtra$GuiTypePixelmon(GuiCreationExtra this$0, String name) {
      super(this$0, name);
      this.this$0 = this$0;
   }

   public void initGui() {
      GuiCustomScroll scroll = new GuiCustomScroll(this.this$0, 1);
      scroll.setSize(120, 200);
      scroll.guiLeft = this.this$0.guiLeft + 120;
      scroll.guiTop = this.this$0.guiTop + 20;
      this.this$0.addScroll(scroll);
      scroll.setList(PixelmonHelper.getPixelmonList());
      scroll.setSelected(PixelmonHelper.getName(this.this$0.entity));
   }

   public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
      String name = scroll.getSelected();
      this.this$0.playerdata.setExtra(this.this$0.entity, "name", name);
      GuiCreationExtra.access$000(this.this$0);
   }
}
