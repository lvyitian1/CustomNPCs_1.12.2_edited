package noppes.npcs.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.api.wrapper.WorldWrapper;
import noppes.npcs.controllers.data.ForgeScriptData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil$JsonException;

public class ScriptController {
   public static ScriptController Instance;
   public static boolean HasStart = false;
   private ScriptEngineManager manager;
   public Map<String, String> languages = new HashMap();
   public Map<String, ScriptEngineFactory> factories = new HashMap();
   public Map<String, String> scripts = new HashMap();
   public PlayerScriptData playerScripts = new PlayerScriptData((EntityPlayer)null);
   public ForgeScriptData forgeScripts = new ForgeScriptData();
   public long lastLoaded = 0L;
   public long lastPlayerUpdate = 0L;
   public File dir;
   public NBTTagCompound compound = new NBTTagCompound();
   private boolean loaded = false;
   public boolean shouldSave = false;

   public ScriptController() {
      this.loaded = false;
      Instance = this;
      this.manager = new ScriptEngineManager();

      try {
         Class c = Class.forName("org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory");
         ScriptEngineFactory factory = (ScriptEngineFactory)c.newInstance();
         factory.getScriptEngine();
         this.manager.registerEngineName("kotlin", factory);
         this.manager.registerEngineExtension("ktl", factory);
         this.manager.registerEngineMimeType("application/kotlin", factory);
         this.languages.put(factory.getLanguageName(), ".ktl");
         this.factories.put(factory.getLanguageName().toLowerCase(), factory);
      } catch (Exception var4) {
         ;
      }

      LogWriter.info("Script Engines Available:");

      for(ScriptEngineFactory fac : this.manager.getEngineFactories()) {
         if (!fac.getExtensions().isEmpty() && (fac.getScriptEngine() instanceof Invocable || fac.getLanguageName().equals("lua"))) {
            String ext = "." + ((String)fac.getExtensions().get(0)).toLowerCase();
            LogWriter.info(fac.getLanguageName() + ": " + ext);
            this.languages.put(fac.getLanguageName(), ext);
            this.factories.put(fac.getLanguageName().toLowerCase(), fac);
         }
      }

   }

   public void loadCategories() {
      this.dir = new File(CustomNpcs.getWorldSaveDirectory(), "scripts");
      if (!this.dir.exists()) {
         this.dir.mkdirs();
      }

      if (!this.worldDataFile().exists()) {
         this.shouldSave = true;
      }

      WorldWrapper.tempData.clear();
      this.scripts.clear();

      for(String language : this.languages.keySet()) {
         String ext = (String)this.languages.get(language);
         File scriptDir = new File(this.dir, language.toLowerCase());
         if (!scriptDir.exists()) {
            scriptDir.mkdir();
         } else {
            this.loadDir(scriptDir, "", ext);
         }
      }

      this.lastLoaded = System.currentTimeMillis();
   }

   private void loadDir(File dir, String name, String ext) {
      for(File file : dir.listFiles()) {
         String filename = name + file.getName().toLowerCase();
         if (file.isDirectory()) {
            this.loadDir(file, filename + "/", ext);
         } else if (filename.endsWith(ext)) {
            try {
               this.scripts.put(filename, this.readFile(file));
            } catch (IOException var10) {
               var10.printStackTrace();
            }
         }
      }

   }

   public boolean loadStoredData() {
      this.compound = new NBTTagCompound();
      File file = this.worldDataFile();

      try {
         if (!file.exists()) {
            return false;
         } else {
            this.compound = NBTJsonUtil.LoadFile(file);
            this.shouldSave = false;
            return true;
         }
      } catch (Exception var3) {
         LogWriter.error("Error loading: " + file.getAbsolutePath(), var3);
         return false;
      }
   }

   private File worldDataFile() {
      return new File(this.dir, "world_data.json");
   }

   private File playerScriptsFile() {
      return new File(this.dir, "player_scripts.json");
   }

   private File forgeScriptsFile() {
      return new File(this.dir, "forge_scripts.json");
   }

   public boolean loadPlayerScripts() {
      this.playerScripts.clear();
      File file = this.playerScriptsFile();

      try {
         if (!file.exists()) {
            return false;
         } else {
            this.playerScripts.readFromNBT(NBTJsonUtil.LoadFile(file));
            return true;
         }
      } catch (Exception var3) {
         LogWriter.error("Error loading: " + file.getAbsolutePath(), var3);
         return false;
      }
   }

   public void setPlayerScripts(NBTTagCompound compound) {
      this.playerScripts.readFromNBT(compound);
      File file = this.playerScriptsFile();

      try {
         NBTJsonUtil.SaveFile(file, compound);
         this.lastPlayerUpdate = System.currentTimeMillis();
      } catch (IOException var4) {
         var4.printStackTrace();
      } catch (NBTJsonUtil$JsonException var5) {
         var5.printStackTrace();
      }

   }

   public boolean loadForgeScripts() {
      this.forgeScripts.clear();
      File file = this.forgeScriptsFile();

      try {
         if (!file.exists()) {
            return false;
         } else {
            this.forgeScripts.readFromNBT(NBTJsonUtil.LoadFile(file));
            return true;
         }
      } catch (Exception var3) {
         LogWriter.error("Error loading: " + file.getAbsolutePath(), var3);
         return false;
      }
   }

   public void setForgeScripts(NBTTagCompound compound) {
      this.forgeScripts.readFromNBT(compound);
      File file = this.forgeScriptsFile();

      try {
         NBTJsonUtil.SaveFile(file, compound);
         this.forgeScripts.lastInited = -1L;
      } catch (IOException var4) {
         var4.printStackTrace();
      } catch (NBTJsonUtil$JsonException var5) {
         var5.printStackTrace();
      }

   }

   private String readFile(File file) throws IOException {
      BufferedReader br = new BufferedReader(new FileReader(file));

      String var5;
      try {
         StringBuilder sb = new StringBuilder();

         for(String line = br.readLine(); line != null; line = br.readLine()) {
            sb.append(line);
            sb.append("\n");
         }

         var5 = sb.toString();
      } finally {
         br.close();
      }

      return var5;
   }

   public ScriptEngine getEngineByName(String language) {
      ScriptEngineFactory fac = (ScriptEngineFactory)this.factories.get(language.toLowerCase());
      return fac == null ? null : fac.getScriptEngine();
   }

   public NBTTagList nbtLanguages() {
      NBTTagList list = new NBTTagList();

      for(String language : this.languages.keySet()) {
         NBTTagCompound compound = new NBTTagCompound();
         NBTTagList scripts = new NBTTagList();

         for(String script : this.getScripts(language)) {
            scripts.appendTag(new NBTTagString(script));
         }

         compound.setTag("Scripts", scripts);
         compound.setString("Language", language);
         list.appendTag(compound);
      }

      return list;
   }

   private List<String> getScripts(String language) {
      List<String> list = new ArrayList();
      String ext = (String)this.languages.get(language);
      if (ext == null) {
         return list;
      } else {
         for(String script : this.scripts.keySet()) {
            if (script.endsWith(ext)) {
               list.add(script);
            }
         }

         return list;
      }
   }

   @SubscribeEvent
   public void saveWorld(Save event) {
      if (this.shouldSave && !event.getWorld().isRemote && event.getWorld() == event.getWorld().getMinecraftServer().worlds[0]) {
         try {
            NBTJsonUtil.SaveFile(this.worldDataFile(), this.compound.copy());
         } catch (Exception var3) {
            LogWriter.except(var3);
         }

         this.shouldSave = false;
      }
   }
}
