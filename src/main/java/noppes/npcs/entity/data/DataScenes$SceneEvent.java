package noppes.npcs.entity.data;

public class DataScenes$SceneEvent implements Comparable<DataScenes$SceneEvent> {
   public int ticks = 0;
   public DataScenes$SceneType type;
   public String param = "";

   public String toString() {
      return this.ticks + " " + this.type.name() + " " + this.param;
   }

   public static DataScenes$SceneEvent parse(String str) {
      DataScenes$SceneEvent event = new DataScenes$SceneEvent();
      int i = str.indexOf(" ");
      if (i <= 0) {
         return null;
      } else {
         try {
            event.ticks = Integer.parseInt(str.substring(0, i));
            str = str.substring(i + 1);
         } catch (NumberFormatException var8) {
            return null;
         }

         i = str.indexOf(" ");
         if (i <= 0) {
            return null;
         } else {
            String name = str.substring(0, i);

            for(DataScenes$SceneType type : DataScenes$SceneType.values()) {
               if (name.equalsIgnoreCase(type.name())) {
                  event.type = type;
               }
            }

            if (event.type == null) {
               return null;
            } else {
               event.param = str.substring(i + 1);
               return event;
            }
         }
      }
   }

   @Override
   public int compareTo(DataScenes$SceneEvent o) {
      return this.ticks - o.ticks;
   }

}
