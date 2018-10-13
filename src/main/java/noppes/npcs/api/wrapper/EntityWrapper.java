package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IRayTrace;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.ServerCloneController;

public class EntityWrapper<T extends Entity> implements IEntity {
   protected T entity;
   private Map<String, Object> tempData = new HashMap();
   private IWorld worldWrapper;
   private final IData tempdata = new EntityWrapper$1(this);
   private final IData storeddata = new EntityWrapper$2(this);

   public EntityWrapper(T entity) {
      this.entity = entity;
      this.worldWrapper = NpcAPI.Instance().getIWorld((WorldServer)entity.world);
   }

   public double getX() {
      return this.entity.posX;
   }

   public void setX(double x) {
      this.entity.posX = x;
   }

   public double getY() {
      return this.entity.posY;
   }

   public void setY(double y) {
      this.entity.posY = y;
   }

   public double getZ() {
      return this.entity.posZ;
   }

   public void setZ(double z) {
      this.entity.posZ = z;
   }

   public int getBlockX() {
      return MathHelper.floor(this.entity.posX);
   }

   public int getBlockY() {
      return MathHelper.floor(this.entity.posY);
   }

   public int getBlockZ() {
      return MathHelper.floor(this.entity.posZ);
   }

   public void setPosition(double x, double y, double z) {
      this.entity.setPosition(x, y, z);
   }

   public IWorld getWorld() {
      if (this.entity.world != this.worldWrapper.getMCWorld()) {
         this.worldWrapper = NpcAPI.Instance().getIWorld((WorldServer)this.entity.world);
      }

      return this.worldWrapper;
   }

   public boolean isAlive() {
      return this.entity.isEntityAlive();
   }

   public IData getTempdata() {
      return this.tempdata;
   }

   public IData getStoreddata() {
      return this.storeddata;
   }

   public long getAge() {
      return (long)this.entity.ticksExisted;
   }

   public void despawn() {
      this.entity.isDead = true;
   }

   public void spawn() {
      if (this.worldWrapper.getMCWorld().getEntityFromUuid(this.entity.getUniqueID()) != null) {
         throw new CustomNPCsException("Entity is already spawned", new Object[0]);
      } else {
         this.entity.isDead = false;
         this.worldWrapper.getMCWorld().spawnEntity(this.entity);
      }
   }

   public void kill() {
      this.entity.setDead();
   }

   public boolean inWater() {
      return this.entity.isInsideOfMaterial(Material.WATER);
   }

   public boolean inLava() {
      return this.entity.isInsideOfMaterial(Material.LAVA);
   }

   public boolean inFire() {
      return this.entity.isInsideOfMaterial(Material.FIRE);
   }

   public boolean isBurning() {
      return this.entity.isBurning();
   }

   public void setBurning(int ticks) {
      this.entity.setFire(ticks);
   }

   public void extinguish() {
      this.entity.extinguish();
   }

   public String getTypeName() {
      return EntityList.getEntityString(this.entity);
   }

   public void dropItem(IItemStack item) {
      this.entity.entityDropItem(item.getMCItemStack(), 0.0F);
   }

   public IEntity[] getRiders() {
      List<Entity> list = this.entity.getPassengers();
      IEntity[] riders = new IEntity[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         riders[i] = NpcAPI.Instance().getIEntity((Entity)list.get(i));
      }

      return riders;
   }

   public IRayTrace rayTraceBlock(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
      Vec3d vec3d = this.entity.getPositionEyes(1.0F);
      Vec3d vec3d1 = this.entity.getLook(1.0F);
      Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
      RayTraceResult result = this.entity.world.rayTraceBlocks(vec3d, vec3d2, stopOnLiquid, ignoreBlockWithoutBoundingBox, true);
      return result == null ? null : new RayTraceWrapper(NpcAPI.Instance().getIBlock(this.entity.world, result.getBlockPos()), result.sideHit.getIndex());
   }

   public IEntity[] rayTraceEntities(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
      Vec3d vec3d = this.entity.getPositionEyes(1.0F);
      Vec3d vec3d1 = this.entity.getLook(1.0F);
      Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
      RayTraceResult result = this.entity.world.rayTraceBlocks(vec3d, vec3d2, stopOnLiquid, ignoreBlockWithoutBoundingBox, false);
      if (result != null) {
         vec3d2 = new Vec3d(result.hitVec.x, result.hitVec.y, result.hitVec.z);
      }

      return this.findEntityOnPath(distance, vec3d, vec3d2);
   }

   private IEntity[] findEntityOnPath(double distance, Vec3d vec3d, Vec3d vec3d1) {
      List<Entity> list = this.entity.world.getEntitiesWithinAABBExcludingEntity(this.entity, this.entity.getEntityBoundingBox().grow(distance));
      List<IEntity> result = new ArrayList();

      for(Entity entity1 : list) {
         if (entity1.canBeCollidedWith() && entity1 != this.entity) {
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
            RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
            if (raytraceresult1 != null) {
               result.add(NpcAPI.Instance().getIEntity(entity1));
            }
         }
      }

      result.sort(new EntityWrapper$3(this));
      return (IEntity[])result.toArray(new IEntity[result.size()]);
   }

   public IEntity[] getAllRiders() {
      List<Entity> list = new ArrayList(this.entity.getRecursivePassengers());
      IEntity[] riders = new IEntity[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         riders[i] = NpcAPI.Instance().getIEntity((Entity)list.get(i));
      }

      return riders;
   }

   public void addRider(IEntity entity) {
      if (entity != null) {
         entity.getMCEntity().startRiding(this.entity, true);
      }

   }

   public void clearRiders() {
      this.entity.removePassengers();
   }

   public IEntity getMount() {
      return NpcAPI.Instance().getIEntity(this.entity.getRidingEntity());
   }

   public void setMount(IEntity entity) {
      if (entity == null) {
         this.entity.dismountRidingEntity();
      } else {
         this.entity.startRiding(entity.getMCEntity(), true);
      }

   }

   public void setRotation(float rotation) {
      this.entity.rotationYaw = rotation;
   }

   public float getRotation() {
      return this.entity.rotationYaw;
   }

   public void setPitch(float rotation) {
      this.entity.rotationPitch = rotation;
   }

   public float getPitch() {
      return this.entity.rotationPitch;
   }

   public void knockback(int power, float direction) {
      float v = direction * 3.1415927F / 180.0F;
      this.entity.addVelocity((double)(-MathHelper.sin(v) * (float)power), 0.1D + (double)((float)power * 0.04F), (double)(MathHelper.cos(v) * (float)power));
      this.entity.motionX *= 0.6D;
      this.entity.motionZ *= 0.6D;
      this.entity.velocityChanged = true;
   }

   public boolean isSneaking() {
      return this.entity.isSneaking();
   }

   public boolean isSprinting() {
      return this.entity.isSprinting();
   }

   public T getMCEntity() {
      return this.entity;
   }

   public int getType() {
      return 0;
   }

   public boolean typeOf(int type) {
      return type == 0;
   }

   public String getUUID() {
      return this.entity.getUniqueID().toString();
   }

   public String generateNewUUID() {
      UUID id = UUID.randomUUID();
      this.entity.setUniqueId(id);
      return id.toString();
   }

   public INbt getNbt() {
      return NpcAPI.Instance().getINbt(this.entity.getEntityData());
   }

   public void storeAsClone(int tab, String name) {
      NBTTagCompound compound = new NBTTagCompound();
      if (!this.entity.writeToNBTOptional(compound)) {
         throw new CustomNPCsException("Cannot store dead or mounted entities", new Object[0]);
      } else {
         ServerCloneController.Instance.addClone(compound, name, tab);
      }
   }

   public INbt getEntityNbt() {
      NBTTagCompound compound = new NBTTagCompound();
      this.entity.writeToNBTOptional(compound);
      return NpcAPI.Instance().getINbt(compound);
   }

   public void setEntityNbt(INbt nbt) {
      this.entity.readFromNBT(nbt.getMCNBT());
   }

   public float getHeight() {
      return this.entity.height;
   }

   public float getEyeHeight() {
      return this.entity.getEyeHeight();
   }

   public float getWidth() {
      return this.entity.width;
   }

   public IPos getPos() {
      return new BlockPosWrapper(this.entity.getPosition());
   }

   public void setPos(IPos pos) {
      this.entity.setPosition((double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F));
   }

   public String[] getTags() {
      return (String[])this.entity.getTags().toArray(new String[this.entity.getTags().size()]);
   }

   public void addTag(String tag) {
      this.entity.addTag(tag);
   }

   public boolean hasTag(String tag) {
      return this.entity.getTags().contains(tag);
   }

   public void removeTag(String tag) {
      this.entity.removeTag(tag);
   }

   // $FF: synthetic method
   static Map access$000(EntityWrapper x0) {
      return x0.tempData;
   }
}
