package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IPlayer;

@Cancelable
public class BlockEvent$HarvestedEvent extends BlockEvent {
   public final IPlayer player;

   public BlockEvent$HarvestedEvent(IBlock block, EntityPlayer player) {
      super(block);
      this.player = (IPlayer)NpcAPI.Instance().getIEntity(player);
   }
}
