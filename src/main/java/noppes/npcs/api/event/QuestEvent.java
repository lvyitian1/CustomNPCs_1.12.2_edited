package noppes.npcs.api.event;

import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IQuest;

public class QuestEvent extends CustomNPCsEvent {
   public final IQuest quest;
   public final IPlayer player;

   public QuestEvent(IPlayer player, IQuest quest) {
      this.quest = quest;
      this.player = player;
   }
}
