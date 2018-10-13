package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IPlayer;

@Cancelable
public class PlayerEvent$BreakEvent extends PlayerEvent {
   public final IBlock block;
   public int exp;

   public PlayerEvent$BreakEvent(IPlayer player, IBlock block, int exp) {
      super(player);
      this.block = block;
      this.exp = exp;
   }
}
