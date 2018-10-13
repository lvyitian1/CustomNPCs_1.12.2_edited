package noppes.npcs.api.wrapper;

import java.io.File;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.handler.ICloneHandler;
import noppes.npcs.api.handler.IDialogHandler;
import noppes.npcs.api.handler.IFactionHandler;
import noppes.npcs.api.handler.IQuestHandler;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.LRUHashMap;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.NBTJsonUtil$JsonException;

public class WrapperNpcAPI extends NpcAPI {
   private static final Map<Integer, IWorld> worldCache = new LRUHashMap<Integer, IWorld>(10);
   public static final EventBus EVENT_BUS = new EventBus();
   private static NpcAPI instance = null;

   public static void clearCache() {
      worldCache.clear();
      BlockWrapper.clearCache();
   }

   public IEntity getIEntity(Entity entity) {
      if (entity != null && !entity.world.isRemote) {
         return (IEntity)(entity instanceof EntityNPCInterface ? ((EntityNPCInterface)entity).wrappedNPC : WrapperEntityData.get(entity));
      } else {
         return null;
      }
   }

   public ICustomNpc createNPC(World world) {
      if (world.isRemote) {
         return null;
      } else {
         EntityCustomNpc npc = new EntityCustomNpc(world);
         return npc.wrappedNPC;
      }
   }

   public ICustomNpc spawnNPC(World world, int x, int y, int z) {
      if (world.isRemote) {
         return null;
      } else {
         EntityCustomNpc npc = new EntityCustomNpc(world);
         npc.setPositionAndRotation((double)x + 0.5D, (double)y, (double)z + 0.5D, 0.0F, 0.0F);
         npc.ais.setStartPos(new BlockPos(x, y, z));
         npc.setHealth(npc.getMaxHealth());
         world.spawnEntity(npc);
         return npc.wrappedNPC;
      }
   }

   public static NpcAPI Instance() {
      if (instance == null) {
         instance = new WrapperNpcAPI();
      }

      return instance;
   }

   public EventBus events() {
      return EVENT_BUS;
   }

   public IBlock getIBlock(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return BlockWrapper.createNew(world, pos, state);
   }

   public IItemStack getIItemStack(ItemStack itemstack) {
      return (IItemStack)itemstack.getCapability(ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY, (EnumFacing)null);
   }

   public IWorld getIWorld(WorldServer world) {
      IWorld w = (IWorld)worldCache.get(Integer.valueOf(world.provider.getDimension()));
      if (w != null) {
         return w;
      } else {
         worldCache.put(Integer.valueOf(world.provider.getDimension()), w = WorldWrapper.createNew(world));
         return w;
      }
   }

   public IWorld getIWorld(int dimensionId) {
      for(WorldServer world : CustomNpcs.Server.worlds) {
         if (world.provider.getDimension() == dimensionId) {
            return this.getIWorld(world);
         }
      }

      throw new CustomNPCsException("Unknown dimension id: " + dimensionId, new Object[0]);
   }

   public IContainer getIContainer(IInventory inventory) {
      return new ContainerWrapper(inventory);
   }

   public IContainer getIContainer(Container container) {
      return new ContainerWrapper(container);
   }

   public IFactionHandler getFactions() {
      this.checkWorld();
      return FactionController.instance;
   }

   private void checkWorld() {
      if (CustomNpcs.Server == null || CustomNpcs.Server.isServerStopped()) {
         throw new CustomNPCsException("No world is loaded right now", new Object[0]);
      }
   }

   public IRecipeHandler getRecipes() {
      this.checkWorld();
      return RecipeController.instance;
   }

   public IQuestHandler getQuests() {
      this.checkWorld();
      return QuestController.instance;
   }

   public IWorld[] getIWorlds() {
      this.checkWorld();
      IWorld[] worlds = new IWorld[CustomNpcs.Server.worlds.length];

      for(int i = 0; i < CustomNpcs.Server.worlds.length; ++i) {
         worlds[i] = this.getIWorld(CustomNpcs.Server.worlds[i]);
      }

      return worlds;
   }

   public IPos getIPos(double x, double y, double z) {
      return new BlockPosWrapper(new BlockPos(x, y, z));
   }

   public File getGlobalDir() {
      return CustomNpcs.Dir;
   }

   public File getWorldDir() {
      return CustomNpcs.getWorldSaveDirectory();
   }

   public void registerCommand(CommandNoppesBase command) {
      CustomNpcs.NoppesCommand.registerCommand(command);
   }

   public INbt getINbt(NBTTagCompound compound) {
      return compound == null ? new NBTWrapper(new NBTTagCompound()) : new NBTWrapper(compound);
   }

   public INbt stringToNbt(String str) {
      if (str != null && !str.isEmpty()) {
         try {
            return this.getINbt(NBTJsonUtil.Convert(str));
         } catch (NBTJsonUtil$JsonException var3) {
            throw new CustomNPCsException(var3, "Failed converting " + str, new Object[0]);
         }
      } else {
         throw new CustomNPCsException("Cant cast empty string to nbt", new Object[0]);
      }
   }

   public IDamageSource getIDamageSource(DamageSource damagesource) {
      return new DamageSourceWrapper(damagesource);
   }

   public IDialogHandler getDialogs() {
      return DialogController.instance;
   }

   public ICloneHandler getClones() {
      return ServerCloneController.Instance;
   }
}
