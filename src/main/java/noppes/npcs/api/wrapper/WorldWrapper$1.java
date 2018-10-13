package noppes.npcs.api.wrapper;

import noppes.npcs.api.entity.data.IData;

class WorldWrapper$1 implements IData {
   // $FF: synthetic field
   final WorldWrapper this$0;

   WorldWrapper$1(WorldWrapper this$0) {
      this.this$0 = this$0;
   }

   public void put(String key, Object value) {
      WorldWrapper.tempData.put(key, value);
   }

   public Object get(String key) {
      return WorldWrapper.tempData.get(key);
   }

   public void remove(String key) {
      WorldWrapper.tempData.remove(key);
   }

   public boolean has(String key) {
      return WorldWrapper.tempData.containsKey(key);
   }

   public void clear() {
      WorldWrapper.tempData.clear();
   }

   public String[] getKeys() {
      return (String[])WorldWrapper.tempData.keySet().toArray(new String[WorldWrapper.tempData.size()]);
   }
}
