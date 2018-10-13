package noppes.npcs.client.gui.model;

import java.util.Comparator;
import net.minecraft.util.text.translation.I18n;

class GuiCreationParts$1 implements Comparator<GuiCreationParts$GuiPart> {
   // $FF: synthetic field
   final GuiCreationParts this$0;

   GuiCreationParts$1(GuiCreationParts this$0) {
      this.this$0 = this$0;
   }

   @Override
   public int compare(GuiCreationParts$GuiPart o1, GuiCreationParts$GuiPart o2) {
      String s1 = I18n.translateToLocal("part." + o1.part.name);
      String s2 = I18n.translateToLocal("part." + o2.part.name);
      return s1.compareToIgnoreCase(s2);
   }

}
