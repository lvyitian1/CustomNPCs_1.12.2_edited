package noppes.npcs.client.gui.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiCreationParts extends GuiCreationScreenInterface implements ITextfieldListener, ICustomScrollListener {
   private GuiCustomScroll scroll;
   private GuiCreationParts$GuiPart[] parts = new GuiCreationParts$GuiPart[]{(new GuiCreationParts$GuiPart(this, EnumParts.EARS)).setTypes(new String[]{"gui.none", "gui.normal", "ears.bunny"}), new GuiCreationParts$GuiPartHorns(this), new GuiCreationParts$GuiPartHair(this), (new GuiCreationParts$GuiPart(this, EnumParts.MOHAWK)).setTypes(new String[]{"gui.none", "1", "2"}).noPlayerOptions(), new GuiCreationParts$GuiPartSnout(this), new GuiCreationParts$GuiPartBeard(this), (new GuiCreationParts$GuiPart(this, EnumParts.FIN)).setTypes(new String[]{"gui.none", "fin.shark", "fin.reptile"}), (new GuiCreationParts$GuiPart(this, EnumParts.BREASTS)).setTypes(new String[]{"gui.none", "1", "2", "3"}).noPlayerOptions(), new GuiCreationParts$GuiPartWings(this), new GuiCreationParts$GuiPartClaws(this), (new GuiCreationParts$GuiPart(this, EnumParts.SKIRT)).setTypes(new String[]{"gui.none", "gui.normal"}), new GuiCreationParts$GuiPartLegs(this), new GuiCreationParts$GuiPartTail(this), new GuiCreationParts$GuiPartEyes(this), new GuiCreationParts$GuiPartParticles(this)};
   private static int selected = 0;

   public GuiCreationParts(EntityNPCInterface npc) {
      super(npc);
      this.active = 2;
      this.closeOnEsc = false;
      Arrays.sort(this.parts, new GuiCreationParts$1(this));
   }

   public void initGui() {
      super.initGui();
      if (this.entity != null) {
         this.openGui(new GuiCreationExtra(this.npc));
      } else {
         if (this.scroll == null) {
            List<String> list = new ArrayList();

            for(GuiCreationParts$GuiPart part : this.parts) {
               list.add(I18n.translateToLocal("part." + part.part.name));
            }

            this.scroll = new GuiCustomScroll(this, 0);
            this.scroll.setUnsortedList(list);
         }

         this.scroll.guiLeft = this.guiLeft;
         this.scroll.guiTop = this.guiTop + 46;
         this.scroll.setSize(100, this.ySize - 74);
         this.addScroll(this.scroll);
         if (this.parts[selected] != null) {
            this.scroll.setSelected(I18n.translateToLocal("part." + this.parts[selected].part.name));
            this.parts[selected].initGui();
         }

      }
   }

   protected void actionPerformed(GuiButton btn) {
      super.actionPerformed(btn);
      if (this.parts[selected] != null) {
         this.parts[selected].actionPerformed(btn);
      }

   }

   public void unFocused(GuiNpcTextField textfield) {
      if (textfield.getId() == 23) {
         ;
      }

   }

   public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
      if (scroll.selected >= 0) {
         selected = scroll.selected;
         this.initGui();
      }

   }
}
