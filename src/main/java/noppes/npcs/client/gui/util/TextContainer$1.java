package noppes.npcs.client.gui.util;

import java.util.Comparator;

final class TextContainer$1 implements Comparator<TextContainer$MarkUp> {
   @Override
   public int compare(TextContainer$MarkUp o1, TextContainer$MarkUp o2) {
      if (o1.start > o2.start) {
         return 1;
      } else {
         return o1.start < o2.start ? -1 : 0;
      }
   }
}
