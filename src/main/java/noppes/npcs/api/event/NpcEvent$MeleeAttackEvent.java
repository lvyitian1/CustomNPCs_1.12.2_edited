package noppes.npcs.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntityLivingBase;

@Cancelable
public class NpcEvent$MeleeAttackEvent extends NpcEvent {
   public final IEntityLivingBase target;
   public float damage;

   public NpcEvent$MeleeAttackEvent(ICustomNpc npc, EntityLivingBase target, float damage) {
      super(npc);
      this.target = (IEntityLivingBase)NpcAPI.Instance().getIEntity(target);
      this.damage = damage;
   }
}
