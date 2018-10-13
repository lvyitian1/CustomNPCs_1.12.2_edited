package noppes.npcs.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;

public interface IAnimal<T extends EntityAnimal> extends IEntityLiving<T> {
   T getMCEntity();


}
