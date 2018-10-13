package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemScripted;

@Cancelable
public class ItemEvent$TossedEvent extends ItemEvent {
   public IEntityItem entity;
   public IPlayer player;

   public ItemEvent$TossedEvent(IItemScripted item, IPlayer player, IEntityItem entity) {
      super(item);
      this.entity = entity;
      this.player = player;
   }
}
