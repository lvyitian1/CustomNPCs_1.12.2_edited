package noppes.npcs;

import java.util.ArrayList;
import java.util.List;

public class CustomNpcsPermissions$Permission {
   private static final List<String> permissions = new ArrayList();
   public String name;
   public boolean defaultValue = true;

   public CustomNpcsPermissions$Permission(String name) {
      this.name = name;
      if (!permissions.contains(name)) {
         permissions.add(name);
      }

   }

   public CustomNpcsPermissions$Permission(String name, boolean defaultValue) {
      this.name = name;
      if (!permissions.contains(name)) {
         permissions.add(name);
      }

      this.defaultValue = defaultValue;
   }

   // $FF: synthetic method
   static List access$000() {
      return permissions;
   }
}
