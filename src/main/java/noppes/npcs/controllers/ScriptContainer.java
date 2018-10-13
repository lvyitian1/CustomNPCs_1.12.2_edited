package noppes.npcs.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.constants.ParticleType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.constants.SideType;
import noppes.npcs.api.constants.TacticalType;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.constants.EnumScriptType;

public class ScriptContainer {
   private static final String lock = "lock";
   public static ScriptContainer Current;
   private static String CurrentType;
   private static final HashMap<String, Object> Data = new HashMap();
   public String fullscript = "";
   public String script = "";
   public TreeMap<Long, String> console = new TreeMap();
   public boolean errored = false;
   public List<String> scripts = new ArrayList();
   private HashSet<String> unknownFunctions = new HashSet();
   public long lastCreated = 0L;
   private String currentScriptLanguage = null;
   private ScriptEngine engine = null;
   private IScriptHandler handler = null;
   private boolean init = false;
   private static Method luaCoerce;
   private static Method luaCall;

   private static void FillMap(Class c) {
      try {
         Data.put(c.getSimpleName(), c.newInstance());
      } catch (Exception var8) {
         ;
      }

      Field[] declaredFields = c.getDeclaredFields();

      for(Field field : declaredFields) {
         try {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == Integer.TYPE) {
               Data.put(c.getSimpleName() + "_" + field.getName(), Integer.valueOf(field.getInt((Object)null)));
            }
         } catch (Exception var7) {
            ;
         }
      }

   }

   public ScriptContainer(IScriptHandler handler) {
      this.handler = handler;
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.script = compound.getString("Script");
      this.console = NBTTags.GetLongStringMap(compound.getTagList("Console", 10));
      this.scripts = NBTTags.getStringList(compound.getTagList("ScriptList", 10));
      this.lastCreated = 0L;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setString("Script", this.script);
      compound.setTag("Console", NBTTags.NBTLongStringMap(this.console));
      compound.setTag("ScriptList", NBTTags.nbtStringList(this.scripts));
      return compound;
   }

   private String getFullCode() {
      if (!this.init) {
         this.fullscript = this.script;
         if (!this.fullscript.isEmpty()) {
            this.fullscript = this.fullscript + "\n";
         }

         for(String loc : this.scripts) {
            String code = (String)ScriptController.Instance.scripts.get(loc);
            if (code != null && !code.isEmpty()) {
               this.fullscript = this.fullscript + code + "\n";
            }
         }

         this.unknownFunctions = new HashSet();
      }

      return this.fullscript;
   }

   public void run(EnumScriptType type, Event event) {
      this.run(type.function, event);
   }

   public void run(String type, Event event) {
      if (!this.errored && this.hasCode() && !this.unknownFunctions.contains(type) && CustomNpcs.EnableScripting) {
         this.setEngine(this.handler.getLanguage());
         if (this.engine != null) {
            if (ScriptController.Instance.lastLoaded > this.lastCreated) {
               this.lastCreated = ScriptController.Instance.lastLoaded;
               this.init = false;
            }

            synchronized("lock") {
               Current = this;
               CurrentType = type;
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               this.engine.getContext().setWriter(pw);
               this.engine.getContext().setErrorWriter(pw);

               try {
                  if (!this.init) {
                     this.engine.eval(this.getFullCode());
                     this.init = true;
                  }

                  if (this.engine.getFactory().getLanguageName().equals("lua")) {
                     Object ob = this.engine.get(type);
                     if (ob != null) {
                        if (luaCoerce == null) {
                           luaCoerce = Class.forName("org.luaj.vm2.lib.jse.CoerceJavaToLua").getMethod("coerce", Object.class);
                           luaCall = ob.getClass().getMethod("call", Class.forName("org.luaj.vm2.LuaValue"));
                        }

                        luaCall.invoke(ob, luaCoerce.invoke((Object)null, event));
                     } else {
                        this.unknownFunctions.add(type);
                     }
                  } else {
                     ((Invocable)this.engine).invokeFunction(type, event);
                  }
               } catch (NoSuchMethodException var13) {
                  this.unknownFunctions.add(type);
               } catch (Error | Exception var14) {
                  this.errored = true;
                  var14.printStackTrace(pw);
                  NoppesUtilServer.NotifyOPs(this.handler.noticeString() + " script errored");
               } finally {
                  this.appandConsole(sw.getBuffer().toString().trim());
                  pw.close();
                  Current = null;
               }

            }
         }
      }
   }

   public void appandConsole(String message) {
      if (message != null && !message.isEmpty()) {
         long time = System.currentTimeMillis();
         if (this.console.containsKey(Long.valueOf(time))) {
            message = (String)this.console.get(Long.valueOf(time)) + "\n" + message;
         }

         this.console.put(Long.valueOf(time), message);

         while(this.console.size() > 40) {
            this.console.remove(this.console.firstKey());
         }

      }
   }

   public boolean hasCode() {
      return !this.getFullCode().isEmpty();
   }

   public void setEngine(String scriptLanguage) {
      if (this.currentScriptLanguage == null || !this.currentScriptLanguage.equals(scriptLanguage)) {
         this.engine = ScriptController.Instance.getEngineByName(scriptLanguage);
         if (this.engine == null) {
            this.errored = true;
         } else {
            for(Entry<String, Object> entry : Data.entrySet()) {
               this.engine.put((String)entry.getKey(), entry.getValue());
            }

            this.engine.put("dump", new ScriptContainer$Dump(this));
            this.engine.put("log", new ScriptContainer$Log(this));
            this.currentScriptLanguage = scriptLanguage;
            this.init = false;
         }
      }
   }

   public boolean isValid() {
      return this.init && !this.errored;
   }

   static {
      FillMap(AnimationType.class);
      FillMap(EntityType.class);
      FillMap(RoleType.class);
      FillMap(JobType.class);
      FillMap(SideType.class);
      FillMap(TacticalType.class);
      FillMap(PotionType.class);
      FillMap(ParticleType.class);
      Data.put("PosZero", new BlockPosWrapper(BlockPos.ORIGIN));
   }
}
