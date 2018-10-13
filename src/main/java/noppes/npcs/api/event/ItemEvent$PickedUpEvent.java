package noppes.npcs.api.event;

import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemScripted;

public class ItemEvent$PickedUpEvent extends ItemEvent {
   public IEntityItem entity;
   public IPlayer player;

   public ItemEvent$PickedUpEvent(IItemScripted item, IPlayer player, IEntityItem entity) {
      super(item);
      this.entity = entity;
      this.player = player;
   }
}
