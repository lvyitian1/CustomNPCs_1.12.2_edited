package noppes.npcs.api.event;

import net.minecraft.entity.Entity;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;

public class NpcEvent$CollideEvent extends NpcEvent {
   public final IEntity entity;

   public NpcEvent$CollideEvent(ICustomNpc npc, Entity entity) {
      super(npc);
      this.entity = NpcAPI.Instance().getIEntity(entity);
   }
}
