package noppes.npcs.api.event;

import noppes.npcs.api.handler.IRecipeHandler;

public class HandlerEvent$RecipesLoadedEvent extends CustomNPCsEvent {
   public final IRecipeHandler handler;

   public HandlerEvent$RecipesLoadedEvent(IRecipeHandler handler) {
      this.handler = handler;
   }
}
