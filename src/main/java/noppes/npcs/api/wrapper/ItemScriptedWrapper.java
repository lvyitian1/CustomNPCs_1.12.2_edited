package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.items.ItemScripted;

public class ItemScriptedWrapper extends ItemStackWrapper implements IItemScripted, IScriptHandler {
   public List<ScriptContainer> scripts = new ArrayList();
   public String scriptLanguage = "ECMAScript";
   public boolean enabled = false;
   public long lastInited = -1L;
   public boolean updateClient = false;
   public boolean durabilityShow = true;
   public double durabilityValue = 1.0D;
   public int durabilityColor = -1;
   public int itemColor = -1;
   public int stackSize = 64;
   public boolean loaded = false;

   public ItemScriptedWrapper(ItemStack item) {
      super(item);
   }

   public boolean hasTexture(int damage) {
      return ItemScripted.Resources.containsKey(Integer.valueOf(damage));
   }

   public String getTexture(int damage) {
      return (String)ItemScripted.Resources.get(Integer.valueOf(damage));
   }

   public void setTexture(int damage, String texture) {
      if (damage == 0) {
         throw new CustomNPCsException("Can't set texture for 0", new Object[0]);
      } else {
         String old = (String)ItemScripted.Resources.get(Integer.valueOf(damage));
         if (old != texture && (old == null || texture == null || !old.equals(texture))) {
            ItemScripted.Resources.put(Integer.valueOf(damage), texture);
            SyncController.syncScriptItemsEverybody();
         }
      }
   }

   public NBTTagCompound getScriptNBT(NBTTagCompound compound) {
      compound.setTag("Scripts", NBTTags.NBTScript(this.scripts));
      compound.setString("ScriptLanguage", this.scriptLanguage);
      compound.setBoolean("ScriptEnabled", this.enabled);
      return compound;
   }

   public NBTTagCompound getNBT() {
      NBTTagCompound compound = super.getNBT();
      this.getScriptNBT(compound);
      compound.setBoolean("DurabilityShow", this.durabilityShow);
      compound.setDouble("DurabilityValue", this.durabilityValue);
      compound.setInteger("DurabilityColor", this.durabilityColor);
      compound.setInteger("ItemColor", this.itemColor);
      compound.setInteger("MaxStackSize", this.stackSize);
      return compound;
   }

   public void setScriptNBT(NBTTagCompound compound) {
      if (compound.hasKey("Scripts")) {
         this.scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
         this.scriptLanguage = compound.getString("ScriptLanguage");
         this.enabled = compound.getBoolean("ScriptEnabled");
      }
   }

   public void setNBT(NBTTagCompound compound) {
      super.setNBT(compound);
      this.setScriptNBT(compound);
      this.durabilityShow = compound.getBoolean("DurabilityShow");
      this.durabilityValue = compound.getDouble("DurabilityValue");
      this.durabilityColor = compound.getInteger("DurabilityColor");
      this.itemColor = compound.getInteger("ItemColor");
      this.stackSize = compound.getInteger("MaxStackSize");
   }

   public int getType() {
      return 6;
   }

   public void runScript(EnumScriptType type, Event event) {
      if (!this.loaded) {
         this.loadScriptData();
         this.loaded = true;
      }

      if (this.isEnabled()) {
         if (ScriptController.Instance.lastLoaded > this.lastInited) {
            this.lastInited = ScriptController.Instance.lastLoaded;
            if (type != EnumScriptType.INIT) {
               EventHooks.onScriptItemInit(this);
            }
         }

         for(ScriptContainer script : this.scripts) {
            script.run(type, event);
         }

      }
   }

   private boolean isEnabled() {
      return this.enabled && ScriptController.HasStart;
   }

   public boolean isClient() {
      return false;
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean bo) {
      this.enabled = bo;
   }

   public String getLanguage() {
      return this.scriptLanguage;
   }

   public void setLanguage(String lang) {
      this.scriptLanguage = lang;
   }

   public List<ScriptContainer> getScripts() {
      return this.scripts;
   }

   public String noticeString() {
      return "ScriptedItem";
   }

   public Map<Long, String> getConsoleText() {
      Map<Long, String> map = new TreeMap();
      int tab = 0;

      for(ScriptContainer script : this.getScripts()) {
         ++tab;

         for(Entry<Long, String> entry : script.console.entrySet()) {
            map.put(entry.getKey(), " tab " + tab + ":\n" + (String)entry.getValue());
         }
      }

      return map;
   }

   public void clearConsole() {
      for(ScriptContainer script : this.getScripts()) {
         script.console.clear();
      }

   }

   public int getMaxStackSize() {
      return this.stackSize;
   }

   public void setMaxStackSize(int size) {
      if (size >= 1 && size <= 64) {
         this.stackSize = size;
      } else {
         throw new CustomNPCsException("Stacksize has to be between 1 and 64", new Object[0]);
      }
   }

   public double getDurabilityValue() {
      return this.durabilityValue;
   }

   public void setDurabilityValue(float value) {
      if ((double)value != this.durabilityValue) {
         this.updateClient = true;
      }

      this.durabilityValue = (double)value;
   }

   public boolean getDurabilityShow() {
      return this.durabilityShow;
   }

   public void setDurabilityShow(boolean bo) {
      if (bo != this.durabilityShow) {
         this.updateClient = true;
      }

      this.durabilityShow = bo;
   }

   public int getDurabilityColor() {
      return this.durabilityColor;
   }

   public void setDurabilityColor(int color) {
      if (color != this.durabilityColor) {
         this.updateClient = true;
      }

      this.durabilityColor = color;
   }

   public int getColor() {
      return this.itemColor;
   }

   public void setColor(int color) {
      if (color != this.itemColor) {
         this.updateClient = true;
      }

      this.itemColor = color;
   }

   public void saveScriptData() {
      NBTTagCompound c = this.item.getTagCompound();
      if (c == null) {
         this.item.setTagCompound(c = new NBTTagCompound());
      }

      c.setTag("ScriptedData", this.getScriptNBT(new NBTTagCompound()));
   }

   public void loadScriptData() {
      NBTTagCompound c = this.item.getTagCompound();
      if (c != null) {
         this.setScriptNBT(c.getCompoundTag("ScriptedData"));
      }
   }
}
