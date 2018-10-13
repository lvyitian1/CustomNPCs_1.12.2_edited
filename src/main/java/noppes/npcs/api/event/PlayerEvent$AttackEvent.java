package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;

@Cancelable
public class PlayerEvent$AttackEvent extends PlayerEvent {
   public final int type;
   public final Object target;

   public PlayerEvent$AttackEvent(IPlayer player, int type, Object target) {
      super(player);
      this.type = type;
      this.target = target;
   }
}
