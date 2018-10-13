package noppes.npcs.api.wrapper;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import noppes.npcs.api.entity.data.IData;

class BlockWrapper$2 implements IData {
   // $FF: synthetic field
   final BlockWrapper this$0;

   BlockWrapper$2(BlockWrapper this$0) {
      this.this$0 = this$0;
   }

   public void put(String key, Object value) {
      NBTTagCompound compound = this.getNBT();
      if (compound != null) {
         if (value instanceof Number) {
            compound.setDouble(key, ((Number)value).doubleValue());
         } else if (value instanceof String) {
            compound.setString(key, (String)value);
         }

      }
   }

   public Object get(String key) {
      NBTTagCompound compound = this.getNBT();
      if (compound == null) {
         return null;
      } else if (!compound.hasKey(key)) {
         return null;
      } else {
         NBTBase base = compound.getTag(key);
         return base instanceof NBTPrimitive ? ((NBTPrimitive)base).getDouble() : ((NBTTagString)base).getString();
      }
   }

   public void remove(String key) {
      NBTTagCompound compound = this.getNBT();
      if (compound != null) {
         compound.removeTag(key);
      }
   }

   public boolean has(String key) {
      NBTTagCompound compound = this.getNBT();
      return compound == null ? false : compound.hasKey(key);
   }

   public void clear() {
      if (this.this$0.tile != null) {
         this.this$0.tile.getTileData().setTag("CustomNPCsData", new NBTTagCompound());
      }
   }

   private NBTTagCompound getNBT() {
      if (this.this$0.tile == null) {
         return null;
      } else {
         NBTTagCompound compound = this.this$0.tile.getTileData().getCompoundTag("CustomNPCsData");
         if (compound.isEmpty() && !this.this$0.tile.getTileData().hasKey("CustomNPCsData")) {
            this.this$0.tile.getTileData().setTag("CustomNPCsData", compound);
         }

         return compound;
      }
   }

   public String[] getKeys() {
      NBTTagCompound compound = this.getNBT();
      return compound == null ? new String[0] : (String[])compound.getKeySet().toArray(new String[compound.getKeySet().size()]);
   }
}
