package noppes.npcs.api.wrapper;

import noppes.npcs.api.entity.data.IData;

class BlockWrapper$1 implements IData {
   // $FF: synthetic field
   final BlockWrapper this$0;

   BlockWrapper$1(BlockWrapper this$0) {
      this.this$0 = this$0;
   }

   public void remove(String key) {
      if (this.this$0.storage != null) {
         this.this$0.storage.tempData.remove(key);
      }
   }

   public void put(String key, Object value) {
      if (this.this$0.storage != null) {
         this.this$0.storage.tempData.put(key, value);
      }
   }

   public boolean has(String key) {
      return this.this$0.storage == null ? false : this.this$0.storage.tempData.containsKey(key);
   }

   public Object get(String key) {
      return this.this$0.storage == null ? null : this.this$0.storage.tempData.get(key);
   }

   public void clear() {
      if (this.this$0.storage != null) {
         this.this$0.storage.tempData.clear();
      }
   }

   public String[] getKeys() {
      return (String[])this.this$0.storage.tempData.keySet().toArray(new String[this.this$0.storage.tempData.size()]);
   }
}
