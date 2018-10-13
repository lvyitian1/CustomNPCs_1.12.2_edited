package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ForgeEvent extends CustomNPCsEvent {
   public final Event event;

   public ForgeEvent(Event event) {
      this.event = event;
   }
}
