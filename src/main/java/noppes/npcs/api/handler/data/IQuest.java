package noppes.npcs.api.handler.data;

import noppes.npcs.api.entity.IPlayer;

public interface IQuest {
   int getId();

   String getName();

   void setName(String var1);

   int getType();

   void setType(int var1);

   String getLogText();

   void setLogText(String var1);

   String getCompleteText();

   void setCompleteText(String var1);

   IQuest getNextQuest();

   void setNextQuest(IQuest var1);

   String[] getObjective(IPlayer var1);

   IQuestCategory getCategory();

   String getNpcName();

   void setNpcName(String var1);

   void save();
}
