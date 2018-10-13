package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IPlayer;

@Cancelable
public class BlockEvent$InteractEvent extends BlockEvent {
   public final IPlayer player;
   public final float hitX;
   public final float hitY;
   public final float hitZ;
   public final int side;

   public BlockEvent$InteractEvent(IBlock block, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
      super(block);
      this.player = (IPlayer)NpcAPI.Instance().getIEntity(player);
      this.hitX = hitX;
      this.hitY = hitY;
      this.hitZ = hitZ;
      this.side = side;
   }
}
