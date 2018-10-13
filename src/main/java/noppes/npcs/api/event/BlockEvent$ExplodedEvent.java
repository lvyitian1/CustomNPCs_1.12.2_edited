package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.block.IBlock;

@Cancelable
public class BlockEvent$ExplodedEvent extends BlockEvent {
   public BlockEvent$ExplodedEvent(IBlock block) {
      super(block);
   }
}
