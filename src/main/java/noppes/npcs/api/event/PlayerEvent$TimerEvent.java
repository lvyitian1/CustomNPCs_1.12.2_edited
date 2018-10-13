package noppes.npcs.api.event;

import noppes.npcs.api.entity.IPlayer;

public class PlayerEvent$TimerEvent extends PlayerEvent {
   public final int id;

   public PlayerEvent$TimerEvent(IPlayer player, int id) {
      super(player);
      this.id = id;
   }
}
