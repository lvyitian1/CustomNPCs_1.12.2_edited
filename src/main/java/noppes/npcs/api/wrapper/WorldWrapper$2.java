package noppes.npcs.api.wrapper;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.controllers.ScriptController;

class WorldWrapper$2 implements IData {
   // $FF: synthetic field
   final WorldWrapper this$0;

   WorldWrapper$2(WorldWrapper this$0) {
      this.this$0 = this$0;
   }

   public void put(String key, Object value) {
      NBTTagCompound compound = ScriptController.Instance.compound;
      if (value instanceof Number) {
         compound.setDouble(key, ((Number)value).doubleValue());
      } else if (value instanceof String) {
         compound.setString(key, (String)value);
      }

      ScriptController.Instance.shouldSave = true;
   }

   public Object get(String key) {
      NBTTagCompound compound = ScriptController.Instance.compound;
      if (!compound.hasKey(key)) {
         return null;
      } else {
         NBTBase base = compound.getTag(key);
         return base instanceof NBTPrimitive ? ((NBTPrimitive)base).getDouble() : ((NBTTagString)base).getString();
      }
   }

   public void remove(String key) {
      ScriptController.Instance.compound.removeTag(key);
      ScriptController.Instance.shouldSave = true;
   }

   public boolean has(String key) {
      return ScriptController.Instance.compound.hasKey(key);
   }

   public void clear() {
      ScriptController.Instance.compound = new NBTTagCompound();
      ScriptController.Instance.shouldSave = true;
   }

   public String[] getKeys() {
      return (String[])ScriptController.Instance.compound.getKeySet().toArray(new String[ScriptController.Instance.compound.getKeySet().size()]);
   }
}
