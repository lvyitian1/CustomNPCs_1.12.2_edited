package noppes.npcs.client.gui.player;

import java.util.Comparator;
import noppes.npcs.controllers.data.PlayerMail;

class GuiMailbox$1 implements Comparator<PlayerMail> {
   // $FF: synthetic field
   final GuiMailbox this$0;

   GuiMailbox$1(GuiMailbox this$0) {
      this.this$0 = this$0;
   }

   public int compare(PlayerMail o1, PlayerMail o2) {
      if (o1.time == o2.time) {
         return 0;
      } else {
         return o1.time > o2.time ? -1 : 1;
      }
   }

}
