package noppes.npcs.api.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;

@Cancelable
public class BlockEvent$EntityFallenUponEvent extends BlockEvent {
   public final IEntity entity;
   public float distanceFallen;

   public BlockEvent$EntityFallenUponEvent(IBlock block, Entity entity, float distance) {
      super(block);
      this.distanceFallen = distance;
      this.entity = NpcAPI.Instance().getIEntity(entity);
   }
}
