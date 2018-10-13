package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;

public class DataScenes {
   private EntityNPCInterface npc;
   public List<DataScenes$SceneContainer> scenes = new ArrayList();
   public static Map<String, DataScenes$SceneState> StartedScenes = new HashMap();
   public static List<DataScenes$SceneContainer> ScenesToRun = new ArrayList();
   private EntityLivingBase owner = null;
   private String ownerScene = null;

   public DataScenes(EntityNPCInterface npc) {
      this.npc = npc;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      NBTTagList list = new NBTTagList();

      for(DataScenes$SceneContainer scene : this.scenes) {
         list.appendTag(scene.writeToNBT(new NBTTagCompound()));
      }

      compound.setTag("Scenes", list);
      return compound;
   }

   public void readFromNBT(NBTTagCompound compound) {
      NBTTagList list = compound.getTagList("Scenes", 10);
      List<DataScenes$SceneContainer> scenes = new ArrayList();

      for(int i = 0; i < list.tagCount(); ++i) {
         DataScenes$SceneContainer scene = new DataScenes$SceneContainer(this);
         scene.readFromNBT(list.getCompoundTagAt(i));
         scenes.add(scene);
      }

      this.scenes = scenes;
   }

   public EntityLivingBase getOwner() {
      return this.owner;
   }

   public static void Toggle(ICommandSender sender, String id) {
      DataScenes$SceneState state = (DataScenes$SceneState)StartedScenes.get(id.toLowerCase());
      if (state != null && !state.paused) {
         state.paused = true;
         NoppesUtilServer.NotifyOPs("Paused scene %s at %s", id, state.ticks);
      } else {
         Start(sender, id);
      }

   }

   public static void Start(ICommandSender sender, String id) {
      DataScenes$SceneState state = (DataScenes$SceneState)StartedScenes.get(id.toLowerCase());
      if (state == null) {
         NoppesUtilServer.NotifyOPs("Started scene %s", id);
         StartedScenes.put(id.toLowerCase(), new DataScenes$SceneState());
      } else if (state.paused) {
         state.paused = false;
         NoppesUtilServer.NotifyOPs("Started scene %s from %s", id, state.ticks);
      }

   }

   public static void Pause(ICommandSender sender, String id) {
      if (id == null) {
         for(DataScenes$SceneState state : StartedScenes.values()) {
            state.paused = true;
         }

         NoppesUtilServer.NotifyOPs("Paused all scenes");
      } else {
         DataScenes$SceneState state = (DataScenes$SceneState)StartedScenes.get(id.toLowerCase());
         state.paused = true;
         NoppesUtilServer.NotifyOPs("Paused scene %s at %s", id, state.ticks);
      }

   }

   public static void Reset(ICommandSender sender, String id) {
      if (id == null) {
         StartedScenes = new HashMap();
         NoppesUtilServer.NotifyOPs("Reset all scene");
      } else if (StartedScenes.remove(id.toLowerCase()) == null) {
         sender.sendMessage(new TextComponentTranslation("Unknown scene %s ", new Object[]{id}));
      } else {
         NoppesUtilServer.NotifyOPs("Reset scene %s", id);
      }

   }

   public void update() {
      for(DataScenes$SceneContainer scene : this.scenes) {
         if (scene.validState()) {
            ScenesToRun.add(scene);
         }
      }

      if (this.owner != null && !StartedScenes.containsKey(this.ownerScene.toLowerCase())) {
         this.owner = null;
         this.ownerScene = null;
      }

   }

   public void addScene(String name) {
      if (!name.isEmpty()) {
         DataScenes$SceneContainer scene = new DataScenes$SceneContainer(this);
         scene.name = name;
         this.scenes.add(scene);
      }
   }

   // $FF: synthetic method
   static EntityNPCInterface access$000(DataScenes x0) {
      return x0.npc;
   }

   // $FF: synthetic method
   static EntityLivingBase access$102(DataScenes x0, EntityLivingBase x1) {
      return x0.owner = x1;
   }

   // $FF: synthetic method
   static String access$202(DataScenes x0, String x1) {
      return x0.ownerScene = x1;
   }
}
