package noppes.npcs.api.wrapper;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import noppes.npcs.api.entity.data.IData;

class ItemStackWrapper$2 implements IData {
   // $FF: synthetic field
   final ItemStackWrapper this$0;

   ItemStackWrapper$2(ItemStackWrapper this$0) {
      this.this$0 = this$0;
   }

   public void put(String key, Object value) {
      if (value instanceof Number) {
         ItemStackWrapper.access$100(this.this$0).setDouble(key, ((Number)value).doubleValue());
      } else if (value instanceof String) {
         ItemStackWrapper.access$100(this.this$0).setString(key, (String)value);
      }

   }

   public Object get(String key) {
      if (!ItemStackWrapper.access$100(this.this$0).hasKey(key)) {
         return null;
      } else {
         NBTBase base = ItemStackWrapper.access$100(this.this$0).getTag(key);
         return base instanceof NBTPrimitive ? ((NBTPrimitive)base).getDouble() : ((NBTTagString)base).getString();
      }
   }

   public void remove(String key) {
      ItemStackWrapper.access$100(this.this$0).removeTag(key);
   }

   public boolean has(String key) {
      return ItemStackWrapper.access$100(this.this$0).hasKey(key);
   }

   public void clear() {
      ItemStackWrapper.access$102(this.this$0, new NBTTagCompound());
   }

   public String[] getKeys() {
      return (String[])ItemStackWrapper.access$100(this.this$0).getKeySet().toArray(new String[ItemStackWrapper.access$100(this.this$0).getKeySet().size()]);
   }
}
