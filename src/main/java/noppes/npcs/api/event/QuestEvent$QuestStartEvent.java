package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IQuest;

@Cancelable
public class QuestEvent$QuestStartEvent extends QuestEvent {
   public QuestEvent$QuestStartEvent(IPlayer player, IQuest quest) {
      super(player, quest);
   }
}
