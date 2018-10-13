package noppes.npcs.controllers.data;

import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.EventHooks;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.quests.QuestInterface;

public class PlayerQuestData {
   public HashMap<Integer, QuestData> activeQuests = new HashMap();
   public HashMap<Integer, Long> finishedQuests = new HashMap();

   public void loadNBTData(NBTTagCompound mainCompound) {
      if (mainCompound != null) {
         NBTTagCompound compound = mainCompound.getCompoundTag("QuestData");
         NBTTagList list = compound.getTagList("CompletedQuests", 10);
         if (list != null) {
            HashMap<Integer, Long> finishedQuests = new HashMap();

            for(int i = 0; i < list.tagCount(); ++i) {
               NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
               finishedQuests.put(Integer.valueOf(nbttagcompound.getInteger("Quest")), Long.valueOf(nbttagcompound.getLong("Date")));
            }

            this.finishedQuests = finishedQuests;
         }

         NBTTagList list2 = compound.getTagList("ActiveQuests", 10);
         if (list2 != null) {
            HashMap<Integer, QuestData> activeQuests = new HashMap();

            for(int i = 0; i < list2.tagCount(); ++i) {
               NBTTagCompound nbttagcompound = list2.getCompoundTagAt(i);
               int id = nbttagcompound.getInteger("Quest");
               Quest quest = (Quest)QuestController.instance.quests.get(Integer.valueOf(id));
               if (quest != null) {
                  QuestData data = new QuestData(quest);
                  data.readEntityFromNBT(nbttagcompound);
                  activeQuests.put(Integer.valueOf(id), data);
               }
            }

            this.activeQuests = activeQuests;
         }

      }
   }

   public void saveNBTData(NBTTagCompound maincompound) {
      NBTTagCompound compound = new NBTTagCompound();
      NBTTagList list = new NBTTagList();
      Iterator list2 = this.finishedQuests.keySet().iterator();

      while(list2.hasNext()) {
         int quest = ((Integer)list2.next()).intValue();
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setInteger("Quest", quest);
         nbttagcompound.setLong("Date", ((Long)this.finishedQuests.get(Integer.valueOf(quest))).longValue());
         list.appendTag(nbttagcompound);
      }

      compound.setTag("CompletedQuests", list);
      NBTTagList list3 = new NBTTagList();
      Iterator var9 = this.activeQuests.keySet().iterator();

      while(var9.hasNext()) {
         int quest = ((Integer)var9.next()).intValue();
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setInteger("Quest", quest);
         ((QuestData)this.activeQuests.get(Integer.valueOf(quest))).writeEntityToNBT(nbttagcompound);
         list3.appendTag(nbttagcompound);
      }

      compound.setTag("ActiveQuests", list3);
      maincompound.setTag("QuestData", compound);
   }

   public QuestData getQuestCompletion(EntityPlayer player, EntityNPCInterface npc) {
      for(QuestData data : this.activeQuests.values()) {
         Quest quest = data.quest;
         if (quest != null && quest.completion == EnumQuestCompletion.Npc && quest.completerNpc.equals(npc.getName()) && quest.questInterface.isCompleted(player)) {
            return data;
         }
      }

      return null;
   }

   public boolean checkQuestCompletion(EntityPlayer player, int type) {
      boolean bo = false;

      for(QuestData data : this.activeQuests.values()) {
         if (data.quest.type == type || type < 0) {
            QuestInterface inter = data.quest.questInterface;
            if (inter.isCompleted(player)) {
               if (!data.isCompleted) {
                  if (!data.quest.complete(player, data)) {
                     Server.sendData((EntityPlayerMP)player, EnumPacketClient.MESSAGE, "quest.completed", data.quest.title, Integer.valueOf(2));
                     Server.sendData((EntityPlayerMP)player, EnumPacketClient.CHAT, "quest.completed", ": ", data.quest.title);
                  }

                  data.isCompleted = true;
                  bo = true;
                  EventHooks.onQuestFinished(PlayerData.get(player).scriptData, data.quest);
               }
            } else {
               data.isCompleted = false;
            }
         }
      }

      return bo;
   }
}
