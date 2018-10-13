package noppes.npcs.api.event;

import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IQuest;

public class QuestEvent$QuestCompletedEvent extends QuestEvent {
   public QuestEvent$QuestCompletedEvent(IPlayer player, IQuest quest) {
      super(player, quest);
   }
}
