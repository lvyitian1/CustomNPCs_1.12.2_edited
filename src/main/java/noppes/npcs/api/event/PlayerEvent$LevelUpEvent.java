package noppes.npcs.api.event;

import noppes.npcs.api.entity.IPlayer;

public class PlayerEvent$LevelUpEvent extends PlayerEvent {
   private final int change;

   public PlayerEvent$LevelUpEvent(IPlayer player, int change) {
      super(player);
      this.change = change;
   }
}
