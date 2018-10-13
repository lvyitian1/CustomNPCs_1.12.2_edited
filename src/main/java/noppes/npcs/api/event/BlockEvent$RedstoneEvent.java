package noppes.npcs.api.event;

import noppes.npcs.api.block.IBlock;

public class BlockEvent$RedstoneEvent extends BlockEvent {
   public final int prevPower;
   public final int power;

   public BlockEvent$RedstoneEvent(IBlock block, int prevPower, int power) {
      super(block);
      this.power = power;
      this.prevPower = prevPower;
   }
}
