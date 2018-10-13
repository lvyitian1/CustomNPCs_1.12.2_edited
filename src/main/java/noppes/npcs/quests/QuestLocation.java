package noppes.npcs.quests;

import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;

public class QuestLocation extends QuestInterface {
   public String location = "";
   public String location2 = "";
   public String location3 = "";

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.location = compound.getString("QuestLocation");
      this.location2 = compound.getString("QuestLocation2");
      this.location3 = compound.getString("QuestLocation3");
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setString("QuestLocation", this.location);
      compound.setString("QuestLocation2", this.location2);
      compound.setString("QuestLocation3", this.location3);
   }

   public boolean isCompleted(EntityPlayer player) {
      PlayerQuestData playerdata = PlayerData.get(player).questData;
      QuestData data = (QuestData)playerdata.activeQuests.get(Integer.valueOf(this.questId));
      return data == null ? false : this.getFound(data, 0);
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
         String found = I18n.translateToLocal("quest.found");
         String notfound = I18n.translateToLocal("quest.notfound");
         if (!this.location.isEmpty()) {
            vec.add(this.location + ": " + (this.getFound(data, 1) ? found : notfound));
         }

         if (!this.location2.isEmpty()) {
            vec.add(this.location2 + ": " + (this.getFound(data, 2) ? found : notfound));
         }

         if (!this.location3.isEmpty()) {
            vec.add(this.location3 + ": " + (this.getFound(data, 3) ? found : notfound));
         }

         return (String[])vec.toArray(new String[vec.size()]);
      }
   }

   public boolean getFound(QuestData data, int i) {
      if (i == 1) {
         return data.extraData.getBoolean("LocationFound");
      } else if (i == 2) {
         return data.extraData.getBoolean("Location2Found");
      } else if (i == 3) {
         return data.extraData.getBoolean("Location3Found");
      } else if (!this.location.isEmpty() && !data.extraData.getBoolean("LocationFound")) {
         return false;
      } else if (!this.location2.isEmpty() && !data.extraData.getBoolean("Location2Found")) {
         return false;
      } else {
         return this.location3.isEmpty() || data.extraData.getBoolean("Location3Found");
      }
   }

   public boolean setFound(QuestData data, String location) {
      if (location.equalsIgnoreCase(this.location) && !data.extraData.getBoolean("LocationFound")) {
         data.extraData.setBoolean("LocationFound", true);
         return true;
      } else if (location.equalsIgnoreCase(this.location2) && !data.extraData.getBoolean("LocationFound2")) {
         data.extraData.setBoolean("Location2Found", true);
         return true;
      } else if (location.equalsIgnoreCase(this.location3) && !data.extraData.getBoolean("LocationFound3")) {
         data.extraData.setBoolean("Location3Found", true);
         return true;
      } else {
         return false;
      }
   }
}
