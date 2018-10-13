package noppes.npcs.controllers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.LogWriter;
import noppes.npcs.Server;
import noppes.npcs.api.handler.IFactionHandler;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.data.Faction;

public class FactionController implements IFactionHandler {
   public HashMap<Integer, Faction> factionsSync = new HashMap();
   public HashMap<Integer, Faction> factions = new HashMap();
   public static FactionController instance = new FactionController();
   private int lastUsedID = 0;

   public FactionController() {
      instance = this;
      this.factions.put(Integer.valueOf(0), new Faction(0, "Friendly", 56576, 2000));
      this.factions.put(Integer.valueOf(1), new Faction(1, "Neutral", 15916288, 1000));
      this.factions.put(Integer.valueOf(2), new Faction(2, "Aggressive", 14483456, 0));
   }

   public void load() {
      this.factions = new HashMap();
      this.lastUsedID = 0;

      try {
         File saveDir = CustomNpcs.getWorldSaveDirectory();
         if (saveDir != null) {
            try {
               File file = new File(saveDir, "factions.dat");
               if (file.exists()) {
                  this.loadFactionsFile(file);
                  return;
               }
            } catch (Exception var9) {
               try {
                  File file = new File(saveDir, "factions.dat_old");
                  if (file.exists()) {
                     this.loadFactionsFile(file);
                     return;
                  }
               } catch (Exception var8) {
                  ;
               }

               return;
            }

            return;
         }
      } finally {
         EventHooks.onGlobalFactionsLoaded(this);
         if (this.factions.isEmpty()) {
            this.factions.put(Integer.valueOf(0), new Faction(0, "Friendly", 56576, 2000));
            this.factions.put(Integer.valueOf(1), new Faction(1, "Neutral", 15916288, 1000));
            this.factions.put(Integer.valueOf(2), new Faction(2, "Aggressive", 14483456, 0));
         }

      }

   }

   private void loadFactionsFile(File file) throws IOException {
      DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
      this.loadFactions(var1);
      var1.close();
   }

   public void loadFactions(DataInputStream stream) throws IOException {
      HashMap<Integer, Faction> factions = new HashMap();
      NBTTagCompound nbttagcompound1 = CompressedStreamTools.read(stream);
      this.lastUsedID = nbttagcompound1.getInteger("lastID");
      NBTTagList list = nbttagcompound1.getTagList("NPCFactions", 10);
      if (list != null) {
         for(int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
            Faction faction = new Faction();
            faction.readNBT(nbttagcompound);
            factions.put(Integer.valueOf(faction.id), faction);
         }
      }

      this.factions = factions;
   }

   public NBTTagCompound getNBT() {
      NBTTagList list = new NBTTagList();
      Iterator nbttagcompound = this.factions.keySet().iterator();

      while(nbttagcompound.hasNext()) {
         int slot = ((Integer)nbttagcompound.next()).intValue();
         Faction faction = (Faction)this.factions.get(Integer.valueOf(slot));
         NBTTagCompound nbtfactions = new NBTTagCompound();
         faction.writeNBT(nbtfactions);
         list.appendTag(nbtfactions);
      }

      NBTTagCompound nbttagcompound2 = new NBTTagCompound();
      nbttagcompound2.setInteger("lastID", this.lastUsedID);
      nbttagcompound2.setTag("NPCFactions", list);
      return nbttagcompound2;
   }

   public void saveFactions() {
      try {
         File saveDir = CustomNpcs.getWorldSaveDirectory();
         File file = new File(saveDir, "factions.dat_new");
         File file1 = new File(saveDir, "factions.dat_old");
         File file2 = new File(saveDir, "factions.dat");
         CompressedStreamTools.writeCompressed(this.getNBT(), new FileOutputStream(file));
         if (file1.exists()) {
            file1.delete();
         }

         file2.renameTo(file1);
         if (file2.exists()) {
            file2.delete();
         }

         file.renameTo(file2);
         if (file.exists()) {
            file.delete();
         }
      } catch (Exception var5) {
         LogWriter.except(var5);
      }

   }

   public Faction getFaction(int faction) {
      return (Faction)this.factions.get(Integer.valueOf(faction));
   }

   public void saveFaction(Faction faction) {
      if (faction.id < 0) {
         for(faction.id = this.getUnusedId(); this.hasName(faction.name); faction.name = faction.name + "_") {
            ;
         }
      } else {
         Faction existing = (Faction)this.factions.get(Integer.valueOf(faction.id));
         if (existing != null && !existing.name.equals(faction.name)) {
            while(this.hasName(faction.name)) {
               faction.name = faction.name + "_";
            }
         }
      }

      this.factions.remove(Integer.valueOf(faction.id));
      this.factions.put(Integer.valueOf(faction.id), faction);
      Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, Integer.valueOf(1), faction.writeNBT(new NBTTagCompound()));
      this.saveFactions();
   }

   public int getUnusedId() {
      if (this.lastUsedID == 0) {
         Iterator var1 = this.factions.keySet().iterator();

         while(var1.hasNext()) {
            int catid = ((Integer)var1.next()).intValue();
            if (catid > this.lastUsedID) {
               this.lastUsedID = catid;
            }
         }
      }

      ++this.lastUsedID;
      return this.lastUsedID;
   }

   public IFaction delete(int id) {
      if (id >= 0 && this.factions.size() > 1) {
         Faction faction = (Faction)this.factions.remove(Integer.valueOf(id));
         if (faction == null) {
            return null;
         } else {
            this.saveFactions();
            faction.id = -1;
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, Integer.valueOf(1), id);
            return faction;
         }
      } else {
         return null;
      }
   }

   public int getFirstFactionId() {
      return ((Integer)this.factions.keySet().iterator().next()).intValue();
   }

   public Faction getFirstFaction() {
      return (Faction)this.factions.values().iterator().next();
   }

   public boolean hasName(String newName) {
      if (newName.trim().isEmpty()) {
         return true;
      } else {
         for(Faction faction : this.factions.values()) {
            if (faction.name.equals(newName)) {
               return true;
            }
         }

         return false;
      }
   }

   public Faction getFactionFromName(String factioname) {
      for(Entry<Integer, Faction> entryfaction : this.factions.entrySet()) {
         if (((Faction)entryfaction.getValue()).name.equalsIgnoreCase(factioname)) {
            return (Faction)entryfaction.getValue();
         }
      }

      return null;
   }

   public String[] getNames() {
      String[] names = new String[this.factions.size()];
      int i = 0;

      for(Faction faction : this.factions.values()) {
         names[i] = faction.name.toLowerCase();
         ++i;
      }

      return names;
   }

   public List<IFaction> list() {
      return new ArrayList(this.factions.values());
   }

   public IFaction create(String name, int color) {
      Faction faction;
      for(faction = new Faction(); this.hasName(name); name = name + "_") {
         ;
      }

      faction.name = name;
      faction.color = color;
      this.saveFaction(faction);
      return faction;
   }

   public IFaction get(int id) {
      return (IFaction)this.factions.get(Integer.valueOf(id));
   }
}
