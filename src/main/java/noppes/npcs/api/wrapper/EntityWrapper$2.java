package noppes.npcs.api.wrapper;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import noppes.npcs.api.entity.data.IData;

class EntityWrapper$2 implements IData {
   // $FF: synthetic field
   final EntityWrapper this$0;

   EntityWrapper$2(EntityWrapper this$0) {
      this.this$0 = this$0;
   }

   public void put(String key, Object value) {
      NBTTagCompound compound = this.getStoredCompound();
      if (value instanceof Number) {
         compound.setDouble(key, ((Number)value).doubleValue());
      } else if (value instanceof String) {
         compound.setString(key, (String)value);
      }

      this.saveStoredCompound(compound);
   }

   public Object get(String key) {
      NBTTagCompound compound = this.getStoredCompound();
      if (!compound.hasKey(key)) {
         return null;
      } else {
         NBTBase base = compound.getTag(key);
         return base instanceof NBTPrimitive ? ((NBTPrimitive)base).getDouble() : ((NBTTagString)base).getString();
      }
   }

   public void remove(String key) {
      NBTTagCompound compound = this.getStoredCompound();
      compound.removeTag(key);
      this.saveStoredCompound(compound);
   }

   public boolean has(String key) {
      return this.getStoredCompound().hasKey(key);
   }

   public void clear() {
      this.this$0.entity.getEntityData().removeTag("CNPCStoredData");
   }

   private NBTTagCompound getStoredCompound() {
      NBTTagCompound compound = this.this$0.entity.getEntityData().getCompoundTag("CNPCStoredData");
      if (compound == null) {
         this.this$0.entity.getEntityData().setTag("CNPCStoredData", compound = new NBTTagCompound());
      }

      return compound;
   }

   private void saveStoredCompound(NBTTagCompound compound) {
      this.this$0.entity.getEntityData().setTag("CNPCStoredData", compound);
   }

   public String[] getKeys() {
      NBTTagCompound compound = this.getStoredCompound();
      return (String[])compound.getKeySet().toArray(new String[compound.getKeySet().size()]);
   }
}
