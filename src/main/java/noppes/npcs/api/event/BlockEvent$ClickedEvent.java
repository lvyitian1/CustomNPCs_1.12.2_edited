package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IPlayer;

public class BlockEvent$ClickedEvent extends BlockEvent {
   public final IPlayer player;

   public BlockEvent$ClickedEvent(IBlock block, EntityPlayer player) {
      super(block);
      this.player = (IPlayer)NpcAPI.Instance().getIEntity(player);
   }
}
