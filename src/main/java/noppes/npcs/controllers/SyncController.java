package noppes.npcs.controllers;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.items.ItemScripted;

public class SyncController {
   public static void syncPlayer(EntityPlayerMP player) {
      NBTTagList list = new NBTTagList();
      new NBTTagCompound();

      for(Faction faction : FactionController.instance.factions.values()) {
         list.appendTag(faction.writeNBT(new NBTTagCompound()));
         if (list.tagCount() > 20) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("Data", list);
            Server.sendData(player, EnumPacketClient.SYNC_ADD, Integer.valueOf(1), compound);
            list = new NBTTagList();
         }
      }

      NBTTagCompound var7 = new NBTTagCompound();
      var7.setTag("Data", list);
      Server.sendData(player, EnumPacketClient.SYNC_END, Integer.valueOf(1), var7);

      for(QuestCategory category : QuestController.instance.categories.values()) {
         Server.sendData(player, EnumPacketClient.SYNC_ADD, Integer.valueOf(3), category.writeNBT(new NBTTagCompound()));
      }

      Server.sendData(player, EnumPacketClient.SYNC_END, Integer.valueOf(3), new NBTTagCompound());

      for(DialogCategory category : DialogController.instance.categories.values()) {
         Server.sendData(player, EnumPacketClient.SYNC_ADD, Integer.valueOf(5), category.writeNBT(new NBTTagCompound()));
      }

      Server.sendData(player, EnumPacketClient.SYNC_END, Integer.valueOf(5), new NBTTagCompound());
      list = new NBTTagList();

      for(RecipeCarpentry category : RecipeController.instance.globalRecipes.values()) {
         list.appendTag(category.writeNBT());
         if (list.tagCount() > 10) {
            var7 = new NBTTagCompound();
            var7.setTag("Data", list);
            Server.sendData(player, EnumPacketClient.SYNC_ADD, Integer.valueOf(6), var7);
            list = new NBTTagList();
         }
      }

      var7 = new NBTTagCompound();
      var7.setTag("Data", list);
      Server.sendData(player, EnumPacketClient.SYNC_END, Integer.valueOf(6), var7);
      list = new NBTTagList();

      for(RecipeCarpentry category : RecipeController.instance.anvilRecipes.values()) {
         list.appendTag(category.writeNBT());
         if (list.tagCount() > 10) {
            var7 = new NBTTagCompound();
            var7.setTag("Data", list);
            Server.sendData(player, EnumPacketClient.SYNC_ADD, Integer.valueOf(7), var7);
            list = new NBTTagList();
         }
      }

      var7 = new NBTTagCompound();
      var7.setTag("Data", list);
      Server.sendData(player, EnumPacketClient.SYNC_END, Integer.valueOf(7), var7);
      PlayerData data = PlayerData.get(player);
      Server.sendData(player, EnumPacketClient.SYNC_END, Integer.valueOf(8), data.getNBT());
      syncScriptItems(player);
   }

   public static void syncAllDialogs(MinecraftServer server) {
      for(DialogCategory category : DialogController.instance.categories.values()) {
         Server.sendToAll(server, EnumPacketClient.SYNC_ADD, Integer.valueOf(5), category.writeNBT(new NBTTagCompound()));
      }

      Server.sendToAll(server, EnumPacketClient.SYNC_END, Integer.valueOf(5), new NBTTagCompound());
   }

   public static void syncAllQuests(MinecraftServer server) {
      for(QuestCategory category : QuestController.instance.categories.values()) {
         Server.sendToAll(server, EnumPacketClient.SYNC_ADD, Integer.valueOf(3), category.writeNBT(new NBTTagCompound()));
      }

      Server.sendToAll(server, EnumPacketClient.SYNC_END, Integer.valueOf(3), new NBTTagCompound());
   }

   public static void syncScriptItems(EntityPlayerMP player) {
      NBTTagCompound comp = new NBTTagCompound();
      comp.setTag("List", NBTTags.nbtIntegerStringMap(ItemScripted.Resources));
      Server.sendData(player, EnumPacketClient.SYNC_END, Integer.valueOf(9), comp);
   }

   public static void syncScriptItemsEverybody() {
      NBTTagCompound comp = new NBTTagCompound();
      comp.setTag("List", NBTTags.nbtIntegerStringMap(ItemScripted.Resources));

      for(EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
         Server.sendData(player, EnumPacketClient.SYNC_END, Integer.valueOf(9), comp);
      }

   }

   public static void clientSync(int synctype, NBTTagCompound compound, boolean syncEnd) {
      if (synctype == 1) {
         NBTTagList list = compound.getTagList("Data", 10);

         for(int i = 0; i < list.tagCount(); ++i) {
            Faction faction = new Faction();
            faction.readNBT(list.getCompoundTagAt(i));
            FactionController.instance.factionsSync.put(Integer.valueOf(faction.id), faction);
         }

         if (syncEnd) {
            FactionController.instance.factions = FactionController.instance.factionsSync;
            FactionController.instance.factionsSync = new HashMap();
         }
      } else if (synctype == 3) {
         if (!compound.isEmpty()) {
            QuestCategory category = new QuestCategory();
            category.readNBT(compound);
            QuestController.instance.categoriesSync.put(Integer.valueOf(category.id), category);
         }

         if (syncEnd) {
            HashMap<Integer, Quest> quests = new HashMap();

            for(QuestCategory category : QuestController.instance.categoriesSync.values()) {
               for(Quest quest : category.quests.values()) {
                  quests.put(Integer.valueOf(quest.id), quest);
               }
            }

            QuestController.instance.categories = QuestController.instance.categoriesSync;
            QuestController.instance.quests = quests;
            QuestController.instance.categoriesSync = new HashMap();
         }
      } else if (synctype == 5) {
         if (!compound.isEmpty()) {
            DialogCategory category = new DialogCategory();
            category.readNBT(compound);
            DialogController.instance.categoriesSync.put(Integer.valueOf(category.id), category);
         }

         if (syncEnd) {
            HashMap<Integer, Dialog> dialogs = new HashMap();

            for(DialogCategory category : DialogController.instance.categoriesSync.values()) {
               for(Dialog dialog : category.dialogs.values()) {
                  dialogs.put(Integer.valueOf(dialog.id), dialog);
               }
            }

            DialogController.instance.categories = DialogController.instance.categoriesSync;
            DialogController.instance.dialogs = dialogs;
            DialogController.instance.categoriesSync = new HashMap();
         }
      } else if (synctype == 6) {
         NBTTagList list = compound.getTagList("Data", 10);

         for(int i = 0; i < list.tagCount(); ++i) {
            RecipeCarpentry recipe = RecipeCarpentry.read(list.getCompoundTagAt(i));
            RecipeController.syncRecipes.put(Integer.valueOf(recipe.id), recipe);
         }

         if (syncEnd) {
            RecipeController.instance.globalRecipes = RecipeController.syncRecipes;
            RecipeController.instance.reloadGlobalRecipes();
            RecipeController.syncRecipes = new HashMap();
         }
      } else if (synctype == 7) {
         NBTTagList list = compound.getTagList("Data", 10);

         for(int i = 0; i < list.tagCount(); ++i) {
            RecipeCarpentry recipe = RecipeCarpentry.read(list.getCompoundTagAt(i));
            RecipeController.syncRecipes.put(Integer.valueOf(recipe.id), recipe);
         }

         if (syncEnd) {
            RecipeController.instance.anvilRecipes = RecipeController.syncRecipes;
            RecipeController.syncRecipes = new HashMap();
         }
      }

   }

   public static void clientSyncUpdate(int synctype, NBTTagCompound compound, ByteBuf buffer) {
      if (synctype == 1) {
         Faction faction = new Faction();
         faction.readNBT(compound);
         FactionController.instance.factions.put(Integer.valueOf(faction.id), faction);
      } else if (synctype == 4) {
         DialogCategory category = (DialogCategory)DialogController.instance.categories.get(Integer.valueOf(buffer.readInt()));
         Dialog dialog = new Dialog(category);
         dialog.readNBT(compound);
         DialogController.instance.dialogs.put(Integer.valueOf(dialog.id), dialog);
         category.dialogs.put(Integer.valueOf(dialog.id), dialog);
      } else if (synctype == 5) {
         DialogCategory category = new DialogCategory();
         category.readNBT(compound);
         DialogController.instance.categories.put(Integer.valueOf(category.id), category);
      } else if (synctype == 2) {
         QuestCategory category = (QuestCategory)QuestController.instance.categories.get(Integer.valueOf(buffer.readInt()));
         Quest quest = new Quest(category);
         quest.readNBT(compound);
         QuestController.instance.quests.put(Integer.valueOf(quest.id), quest);
         category.quests.put(Integer.valueOf(quest.id), quest);
      } else if (synctype == 3) {
         QuestCategory category = new QuestCategory();
         category.readNBT(compound);
         QuestController.instance.categories.put(Integer.valueOf(category.id), category);
      } else if (synctype == 6) {
         RecipeCarpentry recipe = RecipeCarpentry.read(compound);
         RecipeController.instance.globalRecipes.put(Integer.valueOf(recipe.id), recipe);
         RecipeController.instance.reloadGlobalRecipes();
      } else if (synctype == 7) {
         RecipeCarpentry recipe = RecipeCarpentry.read(compound);
         RecipeController.instance.anvilRecipes.put(Integer.valueOf(recipe.id), recipe);
      }

   }

   public static void clientSyncRemove(int synctype, int id) {
      if (synctype == 1) {
         FactionController.instance.factions.remove(Integer.valueOf(id));
      } else if (synctype == 4) {
         Dialog dialog = (Dialog)DialogController.instance.dialogs.remove(Integer.valueOf(id));
         dialog.category.dialogs.remove(Integer.valueOf(id));
      } else if (synctype == 5) {
         DialogCategory category = (DialogCategory)DialogController.instance.categories.remove(Integer.valueOf(id));
         DialogController.instance.dialogs.keySet().removeAll(category.dialogs.keySet());
      } else if (synctype == 2) {
         Quest quest = (Quest)QuestController.instance.quests.remove(Integer.valueOf(id));
         quest.category.quests.remove(Integer.valueOf(id));
      } else if (synctype == 3) {
         QuestCategory category = (QuestCategory)QuestController.instance.categories.remove(Integer.valueOf(id));
         QuestController.instance.quests.keySet().removeAll(category.quests.keySet());
      } else if (synctype == 6) {
         RecipeController.instance.globalRecipes.remove(Integer.valueOf(id));
         RecipeController.instance.reloadGlobalRecipes();
      } else if (synctype == 7) {
         RecipeController.instance.anvilRecipes.remove(Integer.valueOf(id));
      }

   }
}
