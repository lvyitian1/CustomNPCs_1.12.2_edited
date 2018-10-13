package noppes.npcs.controllers;

import java.util.function.Function;
import noppes.npcs.LogWriter;

class ScriptContainer$Log implements Function<Object, Void> {
   // $FF: synthetic field
   final ScriptContainer this$0;

   public ScriptContainer$Log(ScriptContainer var1) {
      this.this$0 = var1;
   }

   public Void apply(Object o) {
      this.this$0.appandConsole(o + "");
      LogWriter.info(o + "");
      return null;
   }

}
