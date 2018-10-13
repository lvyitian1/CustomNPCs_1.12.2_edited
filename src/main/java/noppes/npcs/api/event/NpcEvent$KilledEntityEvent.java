package noppes.npcs.api.event;

import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntityLivingBase;

public class NpcEvent$KilledEntityEvent extends NpcEvent {
   public final IEntityLivingBase entity;

   public NpcEvent$KilledEntityEvent(ICustomNpc npc, EntityLivingBase entity) {
      super(npc);
      this.entity = (IEntityLivingBase)NpcAPI.Instance().getIEntity(entity);
   }
}
