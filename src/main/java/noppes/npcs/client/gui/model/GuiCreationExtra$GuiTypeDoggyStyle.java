package noppes.npcs.client.gui.model;

import java.lang.reflect.Method;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.gui.util.GuiButtonBiDirectional;
import noppes.npcs.client.gui.util.GuiNpcButton;

class GuiCreationExtra$GuiTypeDoggyStyle extends GuiCreationExtra$GuiType {
   // $FF: synthetic field
   final GuiCreationExtra this$0;

   public GuiCreationExtra$GuiTypeDoggyStyle(GuiCreationExtra this$0, String name) {
      super(this$0, name);
      this.this$0 = this$0;
   }

   public void initGui() {
      Enum breed = null;

      try {
         Method method = this.this$0.entity.getClass().getMethod("getBreedID");
         breed = (Enum)method.invoke(this.this$0.entity);
      } catch (Exception var3) {
         ;
      }

      this.this$0.addButton(new GuiButtonBiDirectional(11, this.this$0.guiLeft + 120, this.this$0.guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26"}, breed.ordinal()));
   }

   public void actionPerformed(GuiButton button) {
      if (button.id == 11) {
         int breed = ((GuiNpcButton)button).getValue();
         EntityLivingBase entity = this.this$0.playerdata.getEntity(this.this$0.npc);
         this.this$0.playerdata.setExtra(entity, "breed", ((GuiNpcButton)button).getValue() + "");
         GuiCreationExtra.access$000(this.this$0);
      }
   }
}
