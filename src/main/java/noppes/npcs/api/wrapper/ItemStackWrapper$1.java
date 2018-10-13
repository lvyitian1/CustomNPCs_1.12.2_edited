package noppes.npcs.api.wrapper;

import noppes.npcs.api.entity.data.IData;

class ItemStackWrapper$1 implements IData {
   // $FF: synthetic field
   final ItemStackWrapper this$0;

   ItemStackWrapper$1(ItemStackWrapper this$0) {
      this.this$0 = this$0;
   }

   public void put(String key, Object value) {
      ItemStackWrapper.access$000(this.this$0).put(key, value);
   }

   public Object get(String key) {
      return ItemStackWrapper.access$000(this.this$0).get(key);
   }

   public void remove(String key) {
      ItemStackWrapper.access$000(this.this$0).remove(key);
   }

   public boolean has(String key) {
      return ItemStackWrapper.access$000(this.this$0).containsKey(key);
   }

   public void clear() {
      ItemStackWrapper.access$000(this.this$0).clear();
   }

   public String[] getKeys() {
      return (String[])ItemStackWrapper.access$000(this.this$0).keySet().toArray(new String[ItemStackWrapper.access$000(this.this$0).size()]);
   }
}
