package noppes.npcs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface EntityProjectile$IProjectileCallback {
   boolean onImpact(EntityProjectile var1, BlockPos var2, Entity var3);
}
