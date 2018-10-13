package noppes.npcs.api.wrapper;

import noppes.npcs.api.entity.data.IData;

class EntityWrapper$1 implements IData {
   // $FF: synthetic field
   final EntityWrapper this$0;

   EntityWrapper$1(EntityWrapper this$0) {
      this.this$0 = this$0;
   }

   public void put(String key, Object value) {
      EntityWrapper.access$000(this.this$0).put(key, value);
   }

   public Object get(String key) {
      return EntityWrapper.access$000(this.this$0).get(key);
   }

   public void remove(String key) {
      EntityWrapper.access$000(this.this$0).remove(key);
   }

   public boolean has(String key) {
      return EntityWrapper.access$000(this.this$0).containsKey(key);
   }

   public void clear() {
      EntityWrapper.access$000(this.this$0).clear();
   }

   public String[] getKeys() {
      return (String[])EntityWrapper.access$000(this.this$0).keySet().toArray(new String[EntityWrapper.access$000(this.this$0).size()]);
   }
}
