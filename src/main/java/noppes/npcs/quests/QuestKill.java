package noppes.npcs.quests;

import java.util.HashMap;
import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;

public class QuestKill extends QuestInterface {
   public HashMap<String, Integer> targets = new HashMap();

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.targets = NBTTags.getStringIntegerMap(compound.getTagList("QuestDialogs", 10));
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setTag("QuestDialogs", NBTTags.nbtStringIntegerMap(this.targets));
   }

   public boolean isCompleted(EntityPlayer player) {
      PlayerQuestData playerdata = PlayerData.get(player).questData;
      QuestData data = (QuestData)playerdata.activeQuests.get(Integer.valueOf(this.questId));
      if (data == null) {
         return false;
      } else {
         HashMap<String, Integer> killed = this.getKilled(data);
         if (killed.size() != this.targets.size()) {
            return false;
         } else {
            for(String entity : killed.keySet()) {
               if (!this.targets.containsKey(entity) || ((Integer)this.targets.get(entity)).intValue() > ((Integer)killed.get(entity)).intValue()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public void handleComplete(EntityPlayer player) {
   }

   public String[] getQuestLogStatus(EntityPlayer player) {
      Vector<String> vec = new Vector();
      PlayerQuestData playerdata = PlayerData.get(player).questData;
      QuestData data = (QuestData)playerdata.activeQuests.get(Integer.valueOf(this.questId));
      if (data == null) {
         return new String[0];
      } else {
         HashMap<String, Integer> killed = this.getKilled(data);

         for(String entityName : this.targets.keySet()) {
            int amount = 0;
            if (killed.containsKey(entityName)) {
               amount = ((Integer)killed.get(entityName)).intValue();
            }

            String state = amount + "/" + this.targets.get(entityName);
            vec.add(entityName + ": " + state);
         }

         return (String[])vec.toArray(new String[vec.size()]);
      }
   }

   public HashMap<String, Integer> getKilled(QuestData data) {
      return NBTTags.getStringIntegerMap(data.extraData.getTagList("Killed", 10));
   }

   public void setKilled(QuestData data, HashMap<String, Integer> killed) {
      data.extraData.setTag("Killed", NBTTags.nbtStringIntegerMap(killed));
   }
}
