package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.item.IItemScripted;

@Cancelable
public class ItemEvent$SpawnEvent extends ItemEvent {
   public IEntityItem entity;

   public ItemEvent$SpawnEvent(IItemScripted item, IEntityItem entity) {
      super(item);
      this.entity = entity;
   }
}
