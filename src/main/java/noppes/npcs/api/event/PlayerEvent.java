package noppes.npcs.api.event;

import noppes.npcs.api.entity.IPlayer;

public class PlayerEvent extends CustomNPCsEvent {
   public final IPlayer player;

   public PlayerEvent(IPlayer player) {
      this.player = player;
   }
}
