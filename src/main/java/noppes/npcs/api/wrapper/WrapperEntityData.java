package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.LogWriter;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityProjectile;

public class WrapperEntityData implements ICapabilityProvider {
   @CapabilityInject(WrapperEntityData.class)
   public static Capability<WrapperEntityData> ENTITYDATA_CAPABILITY = null;
   public IEntity base;
   private static final ResourceLocation key = new ResourceLocation("customnpcs", "entitydata");

   public WrapperEntityData(IEntity base) {
      this.base = base;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == ENTITYDATA_CAPABILITY;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(this.hasCapability(capability, facing) ? this : null);
   }

   public static IEntity get(Entity entity) {
      if (entity == null) {
         return null;
      } else {
         WrapperEntityData data = (WrapperEntityData)entity.getCapability(ENTITYDATA_CAPABILITY, (EnumFacing)null);
         if (data == null) {
            LogWriter.warn("Unable to get EntityData for " + entity);
            return getData(entity).base;
         } else {
            return data.base;
         }
      }
   }

   public static void register(AttachCapabilitiesEvent<Entity> event) {
      event.addCapability(key, getData((Entity)event.getObject()));
   }

   private static WrapperEntityData getData(Entity entity) {
      if (entity != null && entity.world != null && !entity.world.isRemote) {
         if (entity instanceof EntityPlayerMP) {
            return new WrapperEntityData(new PlayerWrapper((EntityPlayerMP)entity));
         } else if (PixelmonHelper.isPixelmon(entity)) {
            return new WrapperEntityData(new PixelmonWrapper((EntityTameable)entity));
         } else if (entity instanceof EntityAnimal) {
            return new WrapperEntityData(new AnimalWrapper((EntityAnimal)entity));
         } else if (entity instanceof EntityMob) {
            return new WrapperEntityData(new MonsterWrapper((EntityMob)entity));
         } else if (entity instanceof EntityLiving) {
            return new WrapperEntityData(new EntityLivingWrapper((EntityLiving)entity));
         } else if (entity instanceof EntityLivingBase) {
            return new WrapperEntityData(new EntityLivingBaseWrapper((EntityLivingBase)entity));
         } else if (entity instanceof EntityVillager) {
            return new WrapperEntityData(new EntityVillagerWrapper((EntityVillager)entity));
         } else if (entity instanceof EntityItem) {
            return new WrapperEntityData(new EntityItemWrapper((EntityItem)entity));
         } else {
            return entity instanceof EntityProjectile ? new WrapperEntityData(new EntityProjectileWrapper((EntityProjectile)entity)) : new WrapperEntityData(new EntityWrapper(entity));
         }
      } else {
         return null;
      }
   }
}
