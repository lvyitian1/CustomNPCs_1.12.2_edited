package noppes.npcs.api.event;

import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IEntity;

@Cancelable
public class ForgeEvent$EntityEvent extends ForgeEvent {
   public final IEntity entity;

   public ForgeEvent$EntityEvent(EntityEvent event, IEntity entity) {
      super(event);
      this.entity = entity;
   }
}
