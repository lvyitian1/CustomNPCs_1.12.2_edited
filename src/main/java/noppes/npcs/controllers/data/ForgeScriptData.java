package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;

public class ForgeScriptData implements IScriptHandler {
   private List<ScriptContainer> scripts = new ArrayList();
   private String scriptLanguage = "ECMAScript";
   public long lastInited = -1L;
   public boolean hadInteract = true;
   private boolean enabled = false;

   public void clear() {
      this.scripts = new ArrayList();
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
      this.scriptLanguage = compound.getString("ScriptLanguage");
      this.enabled = compound.getBoolean("ScriptEnabled");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setTag("Scripts", NBTTags.NBTScript(this.scripts));
      compound.setString("ScriptLanguage", this.scriptLanguage);
      compound.setBoolean("ScriptEnabled", this.enabled);
      return compound;
   }

   public void runScript(EnumScriptType type, Event event) {
   }

   public void runScript(String type, Event event) {
      if (this.isEnabled()) {
         CustomNpcs.Server.addScheduledTask(() -> {
            if (ScriptController.Instance.lastLoaded > this.lastInited) {
               this.lastInited = ScriptController.Instance.lastLoaded;
               if (!type.equals("init")) {
                  EventHooks.onForgeInit(this);
               }
            }

            for(ScriptContainer script : this.scripts) {
               script.run(type, event);
            }

         });
      }
   }

   public boolean isEnabled() {
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
      return "ForgeScript";
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
}
