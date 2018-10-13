package noppes.npcs.api.event;

import noppes.npcs.api.block.IBlock;

public class BlockEvent$TimerEvent extends BlockEvent {
   public final int id;

   public BlockEvent$TimerEvent(IBlock block, int id) {
      super(block);
      this.id = id;
   }
}
