package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;

@Cancelable
public class PlayerEvent$ChatEvent extends PlayerEvent {
   public String message;

   public PlayerEvent$ChatEvent(IPlayer player, String message) {
      super(player);
      this.message = message;
   }
}
