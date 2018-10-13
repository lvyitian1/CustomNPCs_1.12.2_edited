package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;

@Cancelable
public class PlayerEvent$RangedLaunchedEvent extends PlayerEvent {
   public PlayerEvent$RangedLaunchedEvent(IPlayer player) {
      super(player);
   }
}
