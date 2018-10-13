package noppes.npcs.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;

public class NpcEvent$DiedEvent extends NpcEvent {
   public final IDamageSource damageSource;
   public final String type;
   public final IEntity source;

   public NpcEvent$DiedEvent(ICustomNpc npc, DamageSource damagesource, Entity entity) {
      super(npc);
      this.damageSource = NpcAPI.Instance().getIDamageSource(damagesource);
      this.type = damagesource.damageType;
      this.source = NpcAPI.Instance().getIEntity(entity);
   }
}
