package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.block.IBlock;

@Cancelable
public class BlockEvent$DoorToggleEvent extends BlockEvent {
   public BlockEvent$DoorToggleEvent(IBlock block) {
      super(block);
   }
}
