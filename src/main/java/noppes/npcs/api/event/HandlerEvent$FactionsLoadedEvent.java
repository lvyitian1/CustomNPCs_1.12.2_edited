package noppes.npcs.api.event;

import noppes.npcs.api.handler.IFactionHandler;

public class HandlerEvent$FactionsLoadedEvent extends CustomNPCsEvent {
   public final IFactionHandler handler;

   public HandlerEvent$FactionsLoadedEvent(IFactionHandler handler) {
      this.handler = handler;
   }
}
