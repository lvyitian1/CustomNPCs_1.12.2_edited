package noppes.npcs.api.event;

import noppes.npcs.api.entity.IEntityProjectile;

public class ProjectileEvent$ImpactEvent extends ProjectileEvent {
   public final int type;
   public final Object target;

   public ProjectileEvent$ImpactEvent(IEntityProjectile projectile, int type, Object target) {
      super(projectile);
      this.type = type;
      this.target = target;
   }
}
