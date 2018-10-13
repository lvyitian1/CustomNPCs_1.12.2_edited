package noppes.npcs.quests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.PlayerData;

public class QuestDialog extends QuestInterface {
   public HashMap<Integer, Integer> dialogs = new HashMap();

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.dialogs = NBTTags.getIntegerIntegerMap(compound.getTagList("QuestDialogs", 10));
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setTag("QuestDialogs", NBTTags.nbtIntegerIntegerMap(this.dialogs));
   }

   public boolean isCompleted(EntityPlayer player) {
      Iterator var2 = this.dialogs.values().iterator();

      while(var2.hasNext()) {
         int dialogId = ((Integer)var2.next()).intValue();
         if (!PlayerData.get(player).dialogData.dialogsRead.contains(Integer.valueOf(dialogId))) {
            return false;
         }
      }

      return true;
   }

   public void handleComplete(EntityPlayer player) {
   }

   public String[] getQuestLogStatus(EntityPlayer player) {
      Vector<String> vec = new Vector();
      Iterator var3 = this.dialogs.values().iterator();

      while(var3.hasNext()) {
         int dialogId = ((Integer)var3.next()).intValue();
         Dialog dialog = (Dialog)DialogController.instance.dialogs.get(Integer.valueOf(dialogId));
         if (dialog != null) {
            String title = dialog.title;
            if (PlayerData.get(player).dialogData.dialogsRead.contains(Integer.valueOf(dialogId))) {
               title = title + " (read)";
            } else {
               title = title + " (unread)";
            }

            vec.add(title);
         }
      }

      return (String[])vec.toArray(new String[vec.size()]);
   }
}
