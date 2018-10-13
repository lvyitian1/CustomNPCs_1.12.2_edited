package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.NoppesStringUtils;
import noppes.npcs.Server;
import noppes.npcs.api.handler.IQuestHandler;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.util.NBTJsonUtil;

public class QuestController implements IQuestHandler {
   public HashMap<Integer, QuestCategory> categoriesSync = new HashMap();
   public HashMap<Integer, QuestCategory> categories = new HashMap();
   public HashMap<Integer, Quest> quests = new HashMap();
   public static QuestController instance = new QuestController();
   private int lastUsedCatID = 0;
   private int lastUsedQuestID = 0;

   public QuestController() {
      instance = this;
   }

   public void load() {
      this.categories.clear();
      this.quests.clear();
      this.lastUsedCatID = 0;
      this.lastUsedQuestID = 0;

      try {
         File file = new File(CustomNpcs.getWorldSaveDirectory(), "quests.dat");
         if (file.exists()) {
            this.loadCategoriesOld(file);
            file.delete();
            file = new File(CustomNpcs.getWorldSaveDirectory(), "quests.dat_old");
            if (file.exists()) {
               file.delete();
            }

            return;
         }
      } catch (Exception var10) {
         ;
      }

      File dir = this.getDir();
      if (!dir.exists()) {
         dir.mkdir();
      } else {
         for(File file : dir.listFiles()) {
            if (file.isDirectory()) {
               QuestCategory category = this.loadCategoryDir(file);
               Iterator<Integer> ite = category.quests.keySet().iterator();

               while(ite.hasNext()) {
                  int id = ((Integer)ite.next()).intValue();
                  if (id > this.lastUsedQuestID) {
                     this.lastUsedQuestID = id;
                  }

                  Quest quest = (Quest)category.quests.get(Integer.valueOf(id));
                  if (this.quests.containsKey(Integer.valueOf(id))) {
                     LogWriter.error("Duplicate id " + quest.id + " from category " + category.title);
                     ite.remove();
                  } else {
                     this.quests.put(Integer.valueOf(id), quest);
                  }
               }

               ++this.lastUsedCatID;
               category.id = this.lastUsedCatID;
               this.categories.put(Integer.valueOf(category.id), category);
            }
         }
      }

   }

   private QuestCategory loadCategoryDir(File dir) {
      QuestCategory category = new QuestCategory();
      category.title = dir.getName();

      for(File file : dir.listFiles()) {
         if (file.isFile() && file.getName().endsWith(".json")) {
            try {
               Quest quest = new Quest(category);
               quest.id = Integer.parseInt(file.getName().substring(0, file.getName().length() - 5));
               quest.readNBTPartial(NBTJsonUtil.LoadFile(file));
               category.quests.put(Integer.valueOf(quest.id), quest);
            } catch (Exception var8) {
               LogWriter.error("Error loading: " + file.getAbsolutePath(), var8);
            }
         }
      }

      return category;
   }

   private void loadCategoriesOld(File file) throws Exception {
      NBTTagCompound nbttagcompound1 = CompressedStreamTools.readCompressed(new FileInputStream(file));
      this.lastUsedCatID = nbttagcompound1.getInteger("lastID");
      this.lastUsedQuestID = nbttagcompound1.getInteger("lastQuestID");
      NBTTagList list = nbttagcompound1.getTagList("Data", 10);
      if (list != null) {
         for(int i = 0; i < list.tagCount(); ++i) {
            QuestCategory category = new QuestCategory();
            category.readNBT(list.getCompoundTagAt(i));
            this.categories.put(Integer.valueOf(category.id), category);
            this.saveCategory(category);
            Iterator<Entry<Integer, Quest>> ita = category.quests.entrySet().iterator();

            while(ita.hasNext()) {
               Entry<Integer, Quest> entry = (Entry)ita.next();
               Quest quest = (Quest)entry.getValue();
               quest.id = ((Integer)entry.getKey()).intValue();
               if (this.quests.containsKey(Integer.valueOf(quest.id))) {
                  ita.remove();
               } else {
                  this.saveQuest(category, quest);
               }
            }
         }
      }

   }

   public void removeCategory(int category) {
      QuestCategory cat = (QuestCategory)this.categories.get(Integer.valueOf(category));
      if (cat != null) {
         File dir = new File(this.getDir(), cat.title);
         if (dir.delete()) {
            Iterator var4 = cat.quests.keySet().iterator();

            while(var4.hasNext()) {
               int dia = ((Integer)var4.next()).intValue();
               this.quests.remove(Integer.valueOf(dia));
            }

            this.categories.remove(Integer.valueOf(category));
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, Integer.valueOf(3), category);
         }
      }
   }

   public void saveCategory(QuestCategory category) {
      category.title = NoppesStringUtils.cleanFileName(category.title);
      if (this.categories.containsKey(Integer.valueOf(category.id))) {
         QuestCategory currentCategory = (QuestCategory)this.categories.get(Integer.valueOf(category.id));
         if (!currentCategory.title.equals(category.title)) {
            while(this.containsCategoryName(category.title)) {
               category.title = category.title + "_";
            }

            File newdir = new File(this.getDir(), category.title);
            File olddir = new File(this.getDir(), currentCategory.title);
            if (newdir.exists()) {
               return;
            }

            if (!olddir.renameTo(newdir)) {
               return;
            }
         }

         category.quests = currentCategory.quests;
      } else {
         if (category.id < 0) {
            ++this.lastUsedCatID;
            category.id = this.lastUsedCatID;
         }

         while(this.containsCategoryName(category.title)) {
            category.title = category.title + "_";
         }

         File dir = new File(this.getDir(), category.title);
         if (!dir.exists()) {
            dir.mkdirs();
         }
      }

      this.categories.put(Integer.valueOf(category.id), category);
      Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, Integer.valueOf(3), category.writeNBT(new NBTTagCompound()));
   }

   private boolean containsCategoryName(String name) {
      name = name.toLowerCase();

      for(QuestCategory cat : this.categories.values()) {
         if (cat.title.toLowerCase().equals(name)) {
            return true;
         }
      }

      return false;
   }

   private boolean containsQuestName(QuestCategory category, Quest quest) {
      for(Quest q : category.quests.values()) {
         if (q.id != quest.id && q.title.equalsIgnoreCase(quest.title)) {
            return true;
         }
      }

      return false;
   }

   public void saveQuest(QuestCategory category, Quest quest) {
      if (category != null) {
         while(this.containsQuestName(quest.category, quest)) {
            quest.title = quest.title + "_";
         }

         if (quest.id < 0) {
            ++this.lastUsedQuestID;
            quest.id = this.lastUsedQuestID;
         }

         this.quests.put(Integer.valueOf(quest.id), quest);
         category.quests.put(Integer.valueOf(quest.id), quest);
         File dir = new File(this.getDir(), category.title);
         if (!dir.exists()) {
            dir.mkdirs();
         }

         File file = new File(dir, quest.id + ".json_new");
         File file2 = new File(dir, quest.id + ".json");

         try {
            NBTJsonUtil.SaveFile(file, quest.writeToNBTPartial(new NBTTagCompound()));
            if (file2.exists()) {
               file2.delete();
            }

            file.renameTo(file2);
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, Integer.valueOf(2), quest.writeToNBT(new NBTTagCompound()), category.id);
         } catch (Exception var7) {
            var7.printStackTrace();
         }

      }
   }

   public void removeQuest(Quest quest) {
      File file = new File(new File(this.getDir(), quest.category.title), quest.id + ".json");
      if (file.delete()) {
         this.quests.remove(Integer.valueOf(quest.id));
         quest.category.quests.remove(Integer.valueOf(quest.id));
         Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, Integer.valueOf(2), quest.id);
      }
   }

   private File getDir() {
      return new File(CustomNpcs.getWorldSaveDirectory(), "quests");
   }

   public List<IQuestCategory> categories() {
      return new ArrayList(this.categories.values());
   }

   public IQuest get(int id) {
      return (IQuest)this.quests.get(Integer.valueOf(id));
   }
}
