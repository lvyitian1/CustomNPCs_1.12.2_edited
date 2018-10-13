package noppes.npcs.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public interface IEntityLiving<T extends EntityLiving> extends IEntityLivingBase<T> {
   boolean isNavigating();

   void clearNavigation();

   void navigateTo(double var1, double var3, double var5, double var7);

   void jump();

   T getMCEntity();

}
