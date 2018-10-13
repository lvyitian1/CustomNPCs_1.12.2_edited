package noppes.npcs.client.gui.util;

class TextContainer$LineData {
   public String text;
   public int start;
   public int end;
   // $FF: synthetic field
   final TextContainer this$0;

   public TextContainer$LineData(TextContainer this$0, String text, int start, int end) {
      this.this$0 = this$0;
      this.text = text;
      this.start = start;
      this.end = end;
   }

   public String getFormattedString() {
      StringBuilder builder = new StringBuilder(this.text);
      int found = 0;

      for(TextContainer$MarkUp entry : this.this$0.makeup) {
         if (entry.start >= this.start && entry.start < this.end) {
            builder.insert(entry.start - this.start + found * 2, Character.toString('\uffff') + Character.toString(entry.c));
            ++found;
         }

         if (entry.start < this.start && entry.end > this.start) {
            builder.insert(0, Character.toString('\uffff') + Character.toString(entry.c));
            ++found;
         }

         if (entry.end >= this.start && entry.end < this.end) {
            builder.insert(entry.end - this.start + found * 2, Character.toString('\uffff') + Character.toString('r'));
            ++found;
         }
      }

      return builder.toString();
   }
}
