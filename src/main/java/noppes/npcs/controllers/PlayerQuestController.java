package noppes.npcs.controllers;

import java.util.Iterator;
import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.EventHooks;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.quests.QuestDialog;

public class PlayerQuestController {
   public static boolean hasActiveQuests(EntityPlayer player) {
      PlayerQuestData data = PlayerData.get(player).questData;
      return !data.activeQuests.isEmpty();
   }

   public static boolean isQuestActive(EntityPlayer player, int quest) {
      PlayerQuestData data = PlayerData.get(player).questData;
      return data.activeQuests.containsKey(Integer.valueOf(quest));
   }

   public static boolean isQuestCompleted(EntityPlayer player, int quest) {
      PlayerQuestData data = PlayerData.get(player).questData;
      QuestData q = (QuestData)data.activeQuests.get(Integer.valueOf(quest));
      return q == null ? false : q.isCompleted;
   }

   public static boolean isQuestFinished(EntityPlayer player, int questid) {
      PlayerQuestData data = PlayerData.get(player).questData;
      return data.finishedQuests.containsKey(Integer.valueOf(questid));
   }

   public static void addActiveQuest(Quest quest, EntityPlayer player) {
      PlayerData playerdata = PlayerData.get(player);
      PlayerQuestData data = playerdata.questData;
      if (canQuestBeAccepted(quest, player)) {
         if (EventHooks.onQuestStarted(playerdata.scriptData, quest)) {
            return;
         }

         data.activeQuests.put(Integer.valueOf(quest.id), new QuestData(quest));
         Server.sendData((EntityPlayerMP)player, EnumPacketClient.MESSAGE, "quest.newquest", quest.title, Integer.valueOf(2));
         Server.sendData((EntityPlayerMP)player, EnumPacketClient.CHAT, "quest.newquest", ": ", quest.title);
         playerdata.updateClient = true;
      }

   }

   public static void setQuestFinished(Quest quest, EntityPlayer player) {
      PlayerData playerdata = PlayerData.get(player);
      PlayerQuestData data = playerdata.questData;
      data.activeQuests.remove(Integer.valueOf(quest.id));
      if (quest.repeat != EnumQuestRepeat.RLDAILY && quest.repeat != EnumQuestRepeat.RLWEEKLY) {
         data.finishedQuests.put(Integer.valueOf(quest.id), Long.valueOf(player.world.getTotalWorldTime()));
      } else {
         data.finishedQuests.put(Integer.valueOf(quest.id), Long.valueOf(System.currentTimeMillis()));
      }

      if (quest.repeat != EnumQuestRepeat.NONE && quest.type == 1) {
         QuestDialog questdialog = (QuestDialog)quest.questInterface;
         Iterator var5 = questdialog.dialogs.values().iterator();

         while(var5.hasNext()) {
            int dialog = ((Integer)var5.next()).intValue();
            playerdata.dialogData.dialogsRead.remove(Integer.valueOf(dialog));
         }
      }

      playerdata.updateClient = true;
   }

   public static boolean canQuestBeAccepted(Quest quest, EntityPlayer player) {
      if (quest == null) {
         return false;
      } else {
         PlayerQuestData data = PlayerData.get(player).questData;
         if (data.activeQuests.containsKey(Integer.valueOf(quest.id))) {
            return false;
         } else if (data.finishedQuests.containsKey(Integer.valueOf(quest.id)) && quest.repeat != EnumQuestRepeat.REPEATABLE) {
            if (quest.repeat == EnumQuestRepeat.NONE) {
               return false;
            } else {
               long questTime = ((Long)data.finishedQuests.get(Integer.valueOf(quest.id))).longValue();
               if (quest.repeat == EnumQuestRepeat.MCDAILY) {
                  return player.world.getTotalWorldTime() - questTime >= 24000L;
               } else if (quest.repeat == EnumQuestRepeat.MCWEEKLY) {
                  return player.world.getTotalWorldTime() - questTime >= 168000L;
               } else if (quest.repeat == EnumQuestRepeat.RLDAILY) {
                  return System.currentTimeMillis() - questTime >= 86400000L;
               } else if (quest.repeat == EnumQuestRepeat.RLWEEKLY) {
                  return System.currentTimeMillis() - questTime >= 604800000L;
               } else {
                  return false;
               }
            }
         } else {
            return true;
         }
      }
   }

   public static Vector<Quest> getActiveQuests(EntityPlayer player) {
      Vector<Quest> quests = new Vector();
      PlayerQuestData data = PlayerData.get(player).questData;

      for(QuestData questdata : data.activeQuests.values()) {
         if (questdata.quest != null) {
            quests.add(questdata.quest);
         }
      }

      return quests;
   }
}
