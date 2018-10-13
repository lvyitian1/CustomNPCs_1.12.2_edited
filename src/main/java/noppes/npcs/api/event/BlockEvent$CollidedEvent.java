package noppes.npcs.api.event;

import net.minecraft.entity.Entity;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;

public class BlockEvent$CollidedEvent extends BlockEvent {
   public final IEntity entity;

   public BlockEvent$CollidedEvent(IBlock block, Entity entity) {
      super(block);
      this.entity = NpcAPI.Instance().getIEntity(entity);
   }
}
