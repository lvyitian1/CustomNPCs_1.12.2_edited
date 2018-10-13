package noppes.npcs.api.event;

import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemScripted;

public class ItemEvent$UpdateEvent extends ItemEvent {
   public IPlayer player;

   public ItemEvent$UpdateEvent(IItemScripted item, IPlayer player) {
      super(item);
      this.player = player;
   }
}
