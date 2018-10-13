package noppes.npcs.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;

@Cancelable
public class PlayerEvent$DamagedEntityEvent extends PlayerEvent {
   public final IDamageSource damageSource;
   public final IEntity target;
   public float damage;

   public PlayerEvent$DamagedEntityEvent(IPlayer player, Entity target, float damage, DamageSource damagesource) {
      super(player);
      this.target = NpcAPI.Instance().getIEntity(target);
      this.damage = damage;
      this.damageSource = NpcAPI.Instance().getIDamageSource(damagesource);
   }
}
