package noppes.npcs.api.event;

import noppes.npcs.api.entity.IEntityProjectile;

public class ProjectileEvent extends CustomNPCsEvent {
   public IEntityProjectile projectile;

   public ProjectileEvent(IEntityProjectile projectile) {
      this.projectile = projectile;
   }
}
