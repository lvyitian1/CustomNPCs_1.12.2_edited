package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IDimension;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IScoreboard;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;

public class WorldWrapper implements IWorld {
   public static Map<String, Object> tempData = new HashMap();
   public WorldServer world;
   public IDimension dimension;
   private IData tempdata = new WorldWrapper$1(this);
   private IData storeddata = new WorldWrapper$2(this);

   private WorldWrapper(World world) {
      this.world = (WorldServer)world;
      this.dimension = new DimensionWrapper(world.provider.getDimension(), world.provider.getDimensionType());
   }

   public WorldServer getMCWorld() {
      return this.world;
   }

   public IEntity[] getNearbyEntities(int x, int y, int z, int range, int type) {
      return this.getNearbyEntities(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
   }

   public IEntity[] getNearbyEntities(IPos pos, int range, int type) {
      AxisAlignedBB bb = (new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)).offset(pos.getMCBlockPos()).grow((double)range, (double)range, (double)range);
      List<Entity> entities = this.world.getEntitiesWithinAABB(this.getClassForType(type), bb);
      List<IEntity> list = new ArrayList();

      for(Entity living : entities) {
         list.add(NpcAPI.Instance().getIEntity(living));
      }

      return (IEntity[])list.toArray(new IEntity[list.size()]);
   }

   public IEntity[] getAllEntities(int type) {
      List<Entity> entities = this.world.getEntities(this.getClassForType(type), EntitySelectors.NOT_SPECTATING);
      List<IEntity> list = new ArrayList();

      for(Entity living : entities) {
         list.add(NpcAPI.Instance().getIEntity(living));
      }

      return (IEntity[])list.toArray(new IEntity[list.size()]);
   }

   public IEntity getClosestEntity(int x, int y, int z, int range, int type) {
      return this.getClosestEntity(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
   }

   public IEntity getClosestEntity(IPos pos, int range, int type) {
      AxisAlignedBB bb = (new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)).offset(pos.getMCBlockPos()).grow((double)range, (double)range, (double)range);
      List<Entity> entities = this.world.getEntitiesWithinAABB(this.getClassForType(type), bb);
      double distance = (double)(range * range * range);
      Entity entity = null;

      for(Entity e : entities) {
         double r = pos.getMCBlockPos().distanceSq(e.getPosition());
         if (entity == null) {
            distance = r;
            entity = e;
         } else if (r < distance) {
            distance = r;
            entity = e;
         }
      }

      return NpcAPI.Instance().getIEntity(entity);
   }

   public IEntity getEntity(String uuid) {
      try {
         UUID id = UUID.fromString(uuid);
         Entity e = this.world.getEntityFromUuid(id);
         if (e == null) {
            e = this.world.getPlayerEntityByUUID(id);
         }

         return e == null ? null : NpcAPI.Instance().getIEntity(e);
      } catch (Exception var4) {
         throw new CustomNPCsException("Given uuid was invalid " + uuid, new Object[0]);
      }
   }

   public IEntity createEntityFromNBT(INbt nbt) {
      Entity entity = EntityList.createEntityFromNBT(nbt.getMCNBT(), this.world);
      if (entity == null) {
         throw new CustomNPCsException("Failed to create an entity from given NBT", new Object[0]);
      } else {
         return NpcAPI.Instance().getIEntity(entity);
      }
   }

   public IEntity createEntity(String id) {
      ResourceLocation resource = new ResourceLocation(id);
      Entity entity = EntityList.createEntityByIDFromName(resource, this.world);
      if (entity == null) {
         throw new CustomNPCsException("Failed to create an entity from given id: " + id, new Object[0]);
      } else {
         return NpcAPI.Instance().getIEntity(entity);
      }
   }

   public IPlayer getPlayer(String name) {
      EntityPlayer player = this.world.getPlayerEntityByName(name);
      return player == null ? null : (IPlayer)NpcAPI.Instance().getIEntity(player);
   }

   private Class getClassForType(int type) {
      if (type == -1) {
         return Entity.class;
      } else if (type == 5) {
         return EntityLivingBase.class;
      } else if (type == 1) {
         return EntityPlayer.class;
      } else if (type == 4) {
         return EntityAnimal.class;
      } else if (type == 3) {
         return EntityMob.class;
      } else if (type == 2) {
         return EntityNPCInterface.class;
      } else if (type == 6) {
         return EntityItem.class;
      } else if (type == 7) {
         return EntityProjectile.class;
      } else if (type == 3) {
         return EntityMob.class;
      } else {
         return type == 9 ? EntityVillager.class : Entity.class;
      }
   }

   public long getTime() {
      return this.world.getWorldTime();
   }

   public void setTime(long time) {
      this.world.setWorldTime(time);
   }

   public long getTotalTime() {
      return this.world.getTotalWorldTime();
   }

   public IBlock getBlock(int x, int y, int z) {
      return NpcAPI.Instance().getIBlock(this.world, new BlockPos(x, y, z));
   }

   public boolean isChunkLoaded(int x, int z) {
      return this.world.getChunkProvider().chunkExists(x >> 4, z >> 4);
   }

   public void setBlock(int x, int y, int z, String name, int meta) {
      Block block = Block.getBlockFromName(name);
      if (block == null) {
         throw new CustomNPCsException("There is no such block: %s", new Object[0]);
      } else {
         this.world.setBlockState(new BlockPos(x, y, z), block.getStateFromMeta(meta));
      }
   }

   public void removeBlock(int x, int y, int z) {
      this.world.setBlockToAir(new BlockPos(x, y, z));
   }

   public float getLightValue(int x, int y, int z) {
      return (float)this.world.getLight(new BlockPos(x, y, z)) / 16.0F;
   }

   public IBlock getSpawnPoint() {
      BlockPos pos = this.world.getSpawnCoordinate();
      if (pos == null) {
         pos = this.world.getSpawnPoint();
      }

      return NpcAPI.Instance().getIBlock(this.world, pos);
   }

   public void setSpawnPoint(IBlock block) {
      this.world.setSpawnPoint(new BlockPos(block.getX(), block.getY(), block.getZ()));
   }

   public boolean isDay() {
      return this.world.getWorldTime() % 24000L < 12000L;
   }

   public boolean isRaining() {
      return this.world.getWorldInfo().isRaining();
   }

   public void setRaining(boolean bo) {
      this.world.getWorldInfo().setRaining(bo);
   }

   public void thunderStrike(double x, double y, double z) {
      this.world.addWeatherEffect(new EntityLightningBolt(this.world, x, y, z, false));
   }

   public void spawnParticle(String particle, double x, double y, double z, double dx, double dy, double dz, double speed, int count) {
      EnumParticleTypes particleType = null;

      for(EnumParticleTypes enumParticle : EnumParticleTypes.values()) {
         if (enumParticle.getArgumentCount() > 0) {
            if (particle.startsWith(enumParticle.getParticleName())) {
               particleType = enumParticle;
               break;
            }
         } else if (particle.equals(enumParticle.getParticleName())) {
            particleType = enumParticle;
            break;
         }
      }

      if (particleType != null) {
         this.world.spawnParticle(particleType, x, y, z, count, dx, dy, dz, speed, new int[0]);
      }

   }

   public IData getTempdata() {
      return this.tempdata;
   }

   public IData getStoreddata() {
      return this.storeddata;
   }

   public IItemStack createItem(String name, int damage, int size) {
      Item item = (Item)Item.REGISTRY.getObject(new ResourceLocation(name));
      if (item == null) {
         throw new CustomNPCsException("Unknown item id: " + name, new Object[0]);
      } else {
         return NpcAPI.Instance().getIItemStack(new ItemStack(item, size, damage));
      }
   }

   public IItemStack createItemFromNbt(INbt nbt) {
      ItemStack item = new ItemStack(nbt.getMCNBT());
      if (item.isEmpty()) {
         throw new CustomNPCsException("Failed to create an item from given NBT", new Object[0]);
      } else {
         return NpcAPI.Instance().getIItemStack(item);
      }
   }

   public void explode(double x, double y, double z, float range, boolean fire, boolean grief) {
      this.world.newExplosion((Entity)null, x, y, z, range, fire, grief);
   }

   public IPlayer[] getAllPlayers() {
      List<EntityPlayerMP> list = this.world.getMinecraftServer().getPlayerList().getPlayers();
      IPlayer[] arr = new IPlayer[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         arr[i] = (IPlayer)NpcAPI.Instance().getIEntity((Entity)list.get(i));
      }

      return arr;
   }

   public String getBiomeName(int x, int z) {
      return this.world.getBiomeForCoordsBody(new BlockPos(x, 0, z)).getBiomeName();
   }

   public IEntity spawnClone(double x, double y, double z, int tab, String name) {
      return NpcAPI.Instance().getClones().spawn(x, y, z, tab, name, this);
   }

   public void spawnEntity(IEntity entity) {
      Entity e = entity.getMCEntity();
      if (this.world.getEntityFromUuid(e.getUniqueID()) != null) {
         throw new CustomNPCsException("Entity with this UUID already exists", new Object[0]);
      } else {
         e.setPosition(e.posX, e.posY, e.posZ);
         this.world.spawnEntity(e);
      }
   }

   public IEntity getClone(int tab, String name) {
      return NpcAPI.Instance().getClones().get(tab, name, this);
   }

   public IScoreboard getScoreboard() {
      return new ScoreboardWrapper(this.world.getMinecraftServer());
   }

   public void broadcast(String message) {
      this.world.getMinecraftServer().getPlayerList().sendMessage(new TextComponentString(message));
   }

   public int getRedstonePower(int x, int y, int z) {
      return this.world.getStrongPower(new BlockPos(x, y, z));
   }

   /** @deprecated */
   @Deprecated
   public static IWorld createNew(WorldServer world) {
      return new WorldWrapper(world);
   }

   public IDimension getDimension() {
      return this.dimension;
   }

   public BlockPos getMCBlockPos(int x, int y, int z) {
      return new BlockPos(x, y, z);
   }
}
