package noppes.npcs.api.event;

import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.item.IItemStack;

public class QuestEvent$QuestTurnedInEvent extends QuestEvent {
   public int expReward;
   public IItemStack[] itemRewards = new IItemStack[0];

   public QuestEvent$QuestTurnedInEvent(IPlayer player, IQuest quest) {
      super(player, quest);
   }
}
