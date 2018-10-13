package noppes.npcs.api.wrapper;

import net.minecraft.entity.passive.EntityVillager;
import noppes.npcs.api.entity.IVillager;

public class EntityVillagerWrapper<T extends EntityVillager> extends EntityLivingWrapper<T> implements IVillager {
   public EntityVillagerWrapper(T entity) {
      super(entity);
   }

   public int getProfession() {
      return ((EntityVillager)this.entity).getProfession();
   }

   public String getCareer() {
      //TODO: rlcai
      return "rlcai";
      //return ((EntityVillager)this.entity).getProfessionForge().getCareer(((EntityVillager)this.entity).careerId).getName();
   }

   public int getType() {
      return 9;
   }
}
