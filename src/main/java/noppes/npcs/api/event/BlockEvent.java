package noppes.npcs.api.event;

import noppes.npcs.api.block.IBlock;

public class BlockEvent extends CustomNPCsEvent {
   public IBlock block;

   public BlockEvent(IBlock block) {
      this.block = block;
   }
}
