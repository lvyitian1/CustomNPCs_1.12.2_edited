package noppes.npcs.util;

class NBTJsonUtil$JsonLine {
   private String line;

   public NBTJsonUtil$JsonLine(String line) {
      this.line = line;
   }

   public void removeComma() {
      if (this.line.endsWith(",")) {
         this.line = this.line.substring(0, this.line.length() - 1);
      }

   }

   public boolean reduceTab() {
      int length = this.line.length();
      return length == 1 && (this.line.endsWith("}") || this.line.endsWith("]")) || length == 2 && (this.line.endsWith("},") || this.line.endsWith("],"));
   }

   public boolean increaseTab() {
      return this.line.endsWith("{") || this.line.endsWith("[");
   }

   public String toString() {
      return this.line;
   }

   // $FF: synthetic method
   static String access$000(NBTJsonUtil$JsonLine x0) {
      return x0.line;
   }

   // $FF: synthetic method
   static String access$002(NBTJsonUtil$JsonLine x0, String x1) {
      return x0.line = x1;
   }
}
