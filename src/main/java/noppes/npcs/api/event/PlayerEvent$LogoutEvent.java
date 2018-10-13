package noppes.npcs.api.event;

import noppes.npcs.api.entity.IPlayer;

public class PlayerEvent$LogoutEvent extends PlayerEvent {
   public PlayerEvent$LogoutEvent(IPlayer player) {
      super(player);
   }
}
