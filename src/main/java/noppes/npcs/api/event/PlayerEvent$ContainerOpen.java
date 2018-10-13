package noppes.npcs.api.event;

import noppes.npcs.api.IContainer;
import noppes.npcs.api.entity.IPlayer;

public class PlayerEvent$ContainerOpen extends PlayerEvent {
   public final IContainer container;

   public PlayerEvent$ContainerOpen(IPlayer player, IContainer container) {
      super(player);
      this.container = container;
   }
}
