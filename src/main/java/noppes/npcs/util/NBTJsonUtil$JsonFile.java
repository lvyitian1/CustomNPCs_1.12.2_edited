package noppes.npcs.util;

class NBTJsonUtil$JsonFile {
   private String original;
   private String text;

   public NBTJsonUtil$JsonFile(String text) {
      this.text = text;
      this.original = text;
   }

   public int keyIndex() {
      boolean hasQuote = false;

      for(int i = 0; i < this.text.length(); ++i) {
         char c = this.text.charAt(i);
         if (i == 0 && c == '"') {
            hasQuote = true;
         } else if (hasQuote && c == '"') {
            hasQuote = false;
         }

         if (!hasQuote && c == ':') {
            return i;
         }
      }

      return -1;
   }

   public String cutDirty(int i) {
      String s = this.text.substring(0, i);
      this.text = this.text.substring(i);
      return s;
   }

   public String cut(int i) {
      String s = this.text.substring(0, i);
      this.text = this.text.substring(i).trim();
      return s;
   }

   public String substring(int beginIndex, int endIndex) {
      return this.text.substring(beginIndex, endIndex);
   }

   public int indexOf(String s) {
      return this.text.indexOf(s);
   }

   public String getCurrentPos() {
      int lengthOr = this.original.length();
      int lengthCur = this.text.length();
      int currentPos = lengthOr - lengthCur;
      String done = this.original.substring(0, currentPos);
      String[] lines = done.split("\r\n|\r|\n");
      int pos = 0;
      String line = "";
      if (lines.length > 0) {
         pos = lines[lines.length - 1].length();
         line = this.original.split("\r\n|\r|\n")[lines.length - 1].trim();
      }

      return "Line: " + lines.length + ", Pos: " + pos + ", Text: " + line;
   }

   public boolean startsWith(String... ss) {
      for(String s : ss) {
         if (this.text.startsWith(s)) {
            return true;
         }
      }

      return false;
   }

   public boolean endsWith(String s) {
      return this.text.endsWith(s);
   }
}
