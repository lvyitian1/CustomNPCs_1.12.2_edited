package noppes.npcs.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntityLivingBase;

@Cancelable
public class NpcEvent$TargetEvent extends NpcEvent {
   public IEntityLivingBase entity;

   public NpcEvent$TargetEvent(ICustomNpc npc, EntityLivingBase entity) {
      super(npc);
      this.entity = (IEntityLivingBase)NpcAPI.Instance().getIEntity(entity);
   }
}
