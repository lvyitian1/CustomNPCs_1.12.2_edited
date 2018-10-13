package noppes.npcs.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil$JsonException;

public class LinkedNpcController {
   public static LinkedNpcController Instance;
   public List<LinkedNpcController$LinkedData> list = new ArrayList();

   public LinkedNpcController() {
      Instance = this;
      this.load();
   }

   private void load() {
      try {
         this.loadNpcs();
      } catch (Exception var2) {
         LogWriter.except(var2);
      }

   }

   public File getDir() {
      File dir = new File(CustomNpcs.getWorldSaveDirectory(), "linkednpcs");
      if (!dir.exists()) {
         dir.mkdir();
      }

      return dir;
   }

   private void loadNpcs() {
      LogWriter.info("Loading Linked Npcs");
      File dir = this.getDir();
      if (dir.exists()) {
         List<LinkedNpcController$LinkedData> list = new ArrayList();

         for(File file : dir.listFiles()) {
            if (file.getName().endsWith(".json")) {
               try {
                  NBTTagCompound compound = NBTJsonUtil.LoadFile(file);
                  LinkedNpcController$LinkedData linked = new LinkedNpcController$LinkedData();
                  linked.setNBT(compound);
                  list.add(linked);
               } catch (Exception var9) {
                  LogWriter.error("Error loading: " + file.getAbsolutePath(), var9);
               }
            }
         }

         this.list = list;
      }

      LogWriter.info("Done loading Linked Npcs");
   }

   public void save() {
      for(LinkedNpcController$LinkedData npc : this.list) {
         try {
            this.saveNpc(npc);
         } catch (IOException var4) {
            LogWriter.except(var4);
         }
      }

   }

   private void saveNpc(LinkedNpcController$LinkedData npc) throws IOException {
      File file = new File(this.getDir(), npc.name + ".json_new");
      File file1 = new File(this.getDir(), npc.name + ".json");

      try {
         NBTJsonUtil.SaveFile(file, npc.getNBT());
         if (file1.exists()) {
            file1.delete();
         }

         file.renameTo(file1);
      } catch (NBTJsonUtil$JsonException var5) {
         LogWriter.except(var5);
      }

   }

   public void loadNpcData(EntityNPCInterface npc) {
      if (!npc.linkedName.isEmpty()) {
         LinkedNpcController$LinkedData data = this.getData(npc.linkedName);
         if (data == null) {
            npc.linkedLast = 0L;
            npc.linkedName = "";
            npc.linkedData = null;
         } else {
            npc.linkedData = data;
            if (npc.posX == 0.0D && npc.posY == 0.0D && npc.posX == 0.0D) {
               return;
            }

            npc.linkedLast = data.time;
            List<int[]> points = npc.ais.getMovingPath();
            NBTTagCompound compound = NBTTags.NBTMerge(this.readNpcData(npc), data.data);
            npc.display.readToNBT(compound);
            npc.stats.readToNBT(compound);
            npc.advanced.readToNBT(compound);
            npc.inventory.readEntityFromNBT(compound);
            if (compound.hasKey("ModelData")) {
               ((EntityCustomNpc)npc).modelData.readFromNBT(compound.getCompoundTag("ModelData"));
            }

            npc.ais.readToNBT(compound);
            npc.transform.readToNBT(compound);
            npc.ais.setMovingPath(points);
            npc.updateClient = true;
         }

      }
   }

   private void cleanTags(NBTTagCompound compound) {
      compound.removeTag("MovingPathNew");
   }

   public LinkedNpcController$LinkedData getData(String name) {
      for(LinkedNpcController$LinkedData data : this.list) {
         if (data.name.equalsIgnoreCase(name)) {
            return data;
         }
      }

      return null;
   }

   private NBTTagCompound readNpcData(EntityNPCInterface npc) {
      NBTTagCompound compound = new NBTTagCompound();
      npc.display.writeToNBT(compound);
      npc.inventory.writeEntityToNBT(compound);
      npc.stats.writeToNBT(compound);
      npc.ais.writeToNBT(compound);
      npc.advanced.writeToNBT(compound);
      npc.transform.writeToNBT(compound);
      compound.setTag("ModelData", ((EntityCustomNpc)npc).modelData.writeToNBT());
      return compound;
   }

   public void saveNpcData(EntityNPCInterface npc) {
      NBTTagCompound compound = this.readNpcData(npc);
      this.cleanTags(compound);
      if (!npc.linkedData.data.equals(compound)) {
         npc.linkedData.data = compound;
         npc.linkedData.time = System.currentTimeMillis();
         this.save();
      }
   }

   public void removeData(String name) {
      Iterator<LinkedNpcController$LinkedData> ita = this.list.iterator();

      while(ita.hasNext()) {
         if (((LinkedNpcController$LinkedData)ita.next()).name.equalsIgnoreCase(name)) {
            ita.remove();
         }
      }

      this.save();
   }

   public void addData(String name) {
      if (this.getData(name) == null && !name.isEmpty()) {
         LinkedNpcController$LinkedData data = new LinkedNpcController$LinkedData();
         data.name = name;
         this.list.add(data);
         this.save();
      }
   }
}
