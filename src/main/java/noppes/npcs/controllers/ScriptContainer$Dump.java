package noppes.npcs.controllers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;
import noppes.npcs.NoppesStringUtils;

class ScriptContainer$Dump implements Function<Object, String> {
   // $FF: synthetic field
   final ScriptContainer this$0;

   public ScriptContainer$Dump(ScriptContainer var1) {
      this.this$0 = var1;
   }

   public String apply(Object o) {
      StringBuilder builder = new StringBuilder();
      builder.append(o + ":" + NoppesStringUtils.newLine());

      for(Field field : o.getClass().getFields()) {
         try {
            builder.append(field.getName() + " - " + field.getType().getSimpleName() + ", ");
         } catch (IllegalArgumentException var12) {
            ;
         }
      }

      for(Method method : o.getClass().getMethods()) {
         try {
            String s = method.getName() + "(";

            for(Class c : method.getParameterTypes()) {
               s = s + c.getSimpleName() + ", ";
            }

            if (s.endsWith(", ")) {
               s = s.substring(0, s.length() - 2);
            }

            builder.append(s + "), ");
         } catch (IllegalArgumentException var13) {
            ;
         }
      }

      return builder.toString();
   }

}
