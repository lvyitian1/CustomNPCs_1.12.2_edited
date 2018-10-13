package noppes.npcs.api.event;

import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IFaction;

public class PlayerEvent$FactionUpdateEvent extends PlayerEvent {
   public final IFaction faction;
   public int points;
   public boolean init;

   public PlayerEvent$FactionUpdateEvent(IPlayer player, IFaction faction, int points, boolean init) {
      super(player);
      this.faction = faction;
      this.points = points;
      this.init = init;
   }
}
