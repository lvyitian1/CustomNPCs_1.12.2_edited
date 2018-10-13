package noppes.npcs.api.event;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.IWorld;

@Cancelable
public class ForgeEvent$WorldEvent extends ForgeEvent {
   public final IWorld world;

   public ForgeEvent$WorldEvent(WorldEvent event, IWorld world) {
      super(event);
      this.world = world;
   }
}
