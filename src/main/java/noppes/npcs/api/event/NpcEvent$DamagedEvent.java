package noppes.npcs.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;

@Cancelable
public class NpcEvent$DamagedEvent extends NpcEvent {
   public final IDamageSource damageSource;
   public final IEntity source;
   public float damage;
   public boolean clearTarget = false;

   public NpcEvent$DamagedEvent(ICustomNpc npc, Entity source, float damage, DamageSource damagesource) {
      super(npc);
      this.source = NpcAPI.Instance().getIEntity(source);
      this.damage = damage;
      this.damageSource = NpcAPI.Instance().getIDamageSource(damagesource);
   }
}
