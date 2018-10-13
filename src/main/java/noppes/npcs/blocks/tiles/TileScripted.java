package noppes.npcs.blocks.tiles;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.util.ValueUtil;

public class TileScripted extends TileNpcEntity implements ITickable, IScriptBlockHandler {
   public List<ScriptContainer> scripts = new ArrayList();
   public String scriptLanguage = "ECMAScript";
   public boolean enabled = false;
   private IBlock blockDummy = null;
   public DataTimers timers = new DataTimers(this);
   public long lastInited = -1L;
   private short ticksExisted = 0;
   public ItemStack itemModel = new ItemStack(CustomItems.scripted);
   public Block blockModel = null;
   public boolean needsClientUpdate = false;
   public int powering = 0;
   public int activePowering = 0;
   public int newPower = 0;
   public int prevPower = 0;
   public boolean isPassible = false;
   public boolean isLadder = false;
   public int lightValue = 0;
   public float blockHardness = 5.0F;
   public float blockResistance = 10.0F;
   public int rotationX = 0;
   public int rotationY = 0;
   public int rotationZ = 0;
   public float scaleX = 1.0F;
   public float scaleY = 1.0F;
   public float scaleZ = 1.0F;
   public TileEntity renderTile;
   public boolean renderTileErrored = true;
   public ITickable renderTileUpdate = null;

   public IBlock getBlock() {
      if (this.blockDummy == null) {
         this.blockDummy = new BlockScriptedWrapper(this.getWorld(), this.getBlockType(), this.getPos());
      }

      return this.blockDummy;
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.setNBT(compound);
      this.setDisplayNBT(compound);
      this.timers.readFromNBT(compound);
   }

   public void setNBT(NBTTagCompound compound) {
      this.scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
      this.scriptLanguage = compound.getString("ScriptLanguage");
      this.enabled = compound.getBoolean("ScriptEnabled");
      this.activePowering = this.powering = compound.getInteger("BlockPowering");
      this.prevPower = compound.getInteger("BlockPrevPower");
      if (compound.hasKey("BlockHardness")) {
         this.blockHardness = compound.getFloat("BlockHardness");
         this.blockResistance = compound.getFloat("BlockResistance");
      }

   }

   public void setDisplayNBT(NBTTagCompound compound) {
      this.itemModel = new ItemStack(compound.getCompoundTag("ScriptBlockModel"));
      if (this.itemModel.isEmpty()) {
         this.itemModel = new ItemStack(CustomItems.scripted);
      }

      if (compound.hasKey("ScriptBlockModelBlock")) {
         this.blockModel = Block.getBlockFromName(compound.getString("ScriptBlockModelBlock"));
      }

      this.renderTileUpdate = null;
      this.renderTile = null;
      this.renderTileErrored = false;
      this.lightValue = compound.getInteger("LightValue");
      this.isLadder = compound.getBoolean("IsLadder");
      this.isPassible = compound.getBoolean("IsPassible");
      this.rotationX = compound.getInteger("RotationX");
      this.rotationY = compound.getInteger("RotationY");
      this.rotationZ = compound.getInteger("RotationZ");
      this.scaleX = compound.getFloat("ScaleX");
      this.scaleY = compound.getFloat("ScaleY");
      this.scaleZ = compound.getFloat("ScaleZ");
      if (this.scaleX <= 0.0F) {
         this.scaleX = 1.0F;
      }

      if (this.scaleY <= 0.0F) {
         this.scaleY = 1.0F;
      }

      if (this.scaleZ <= 0.0F) {
         this.scaleZ = 1.0F;
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      this.getNBT(compound);
      this.getDisplayNBT(compound);
      this.timers.writeToNBT(compound);
      return super.writeToNBT(compound);
   }

   public NBTTagCompound getNBT(NBTTagCompound compound) {
      compound.setTag("Scripts", NBTTags.NBTScript(this.scripts));
      compound.setString("ScriptLanguage", this.scriptLanguage);
      compound.setBoolean("ScriptEnabled", this.enabled);
      compound.setInteger("BlockPowering", this.powering);
      compound.setInteger("BlockPrevPower", this.prevPower);
      compound.setFloat("BlockHardness", this.blockHardness);
      compound.setFloat("BlockResistance", this.blockResistance);
      return compound;
   }

   public NBTTagCompound getDisplayNBT(NBTTagCompound compound) {
      NBTTagCompound itemcompound = new NBTTagCompound();
      this.itemModel.writeToNBT(itemcompound);
      if (this.blockModel != null) {
         ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(this.blockModel);
         compound.setString("ScriptBlockModelBlock", resourcelocation == null ? "" : resourcelocation.toString());
      }

      compound.setTag("ScriptBlockModel", itemcompound);
      compound.setInteger("LightValue", this.lightValue);
      compound.setBoolean("IsLadder", this.isLadder);
      compound.setBoolean("IsPassible", this.isPassible);
      compound.setInteger("RotationX", this.rotationX);
      compound.setInteger("RotationY", this.rotationY);
      compound.setInteger("RotationZ", this.rotationZ);
      compound.setFloat("ScaleX", this.scaleX);
      compound.setFloat("ScaleY", this.scaleY);
      compound.setFloat("ScaleZ", this.scaleZ);
      return compound;
   }

   private boolean isEnabled() {
      return this.enabled && ScriptController.HasStart && !this.world.isRemote;
   }

   public void update() {
      if (this.renderTileUpdate != null) {
         try {
            this.renderTileUpdate.update();
         } catch (Exception var2) {
            this.renderTileUpdate = null;
         }
      }

      ++this.ticksExisted;
      if (this.prevPower != this.newPower && this.powering <= 0) {
         EventHooks.onScriptBlockRedstonePower(this, this.prevPower, this.newPower);
         this.prevPower = this.newPower;
      }

      this.timers.update();
      if (this.ticksExisted >= 10) {
         EventHooks.onScriptBlockUpdate(this);
         this.ticksExisted = 0;
         if (this.needsClientUpdate) {
            this.markDirty();
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
            this.needsClientUpdate = false;
         }
      }

   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.handleUpdateTag(pkt.getNbtCompound());
   }

   public void handleUpdateTag(NBTTagCompound tag) {
      int light = this.lightValue;
      this.setDisplayNBT(tag);
      if (light != this.lightValue) {
         this.world.checkLight(this.pos);
      }

   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setInteger("x", this.pos.getX());
      compound.setInteger("y", this.pos.getY());
      compound.setInteger("z", this.pos.getZ());
      this.getDisplayNBT(compound);
      return compound;
   }

   public void setItemModel(ItemStack item, Block b) {
      if (item == null || item.isEmpty()) {
         item = new ItemStack(CustomItems.scripted);
      }

      if (!NoppesUtilPlayer.compareItems(item, this.itemModel, false, false) || b == this.blockModel) {
         this.itemModel = item;
         this.blockModel = b;
         this.needsClientUpdate = true;
      }
   }

   public void setLightValue(int value) {
      if (value != this.lightValue) {
         this.lightValue = ValueUtil.CorrectInt(value, 0, 15);
         this.needsClientUpdate = true;
      }
   }

   public void setRedstonePower(int strength) {
      if (this.powering != strength) {
         this.prevPower = this.activePowering = ValueUtil.CorrectInt(strength, 0, 15);
         this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
         this.powering = this.activePowering;
      }
   }

   public void setScale(float x, float y, float z) {
      if (this.scaleX != x || this.scaleY != y || this.scaleZ != z) {
         this.scaleX = ValueUtil.correctFloat(x, 0.0F, 10.0F);
         this.scaleY = ValueUtil.correctFloat(y, 0.0F, 10.0F);
         this.scaleZ = ValueUtil.correctFloat(z, 0.0F, 10.0F);
         this.needsClientUpdate = true;
      }
   }

   public void setRotation(int x, int y, int z) {
      if (this.rotationX != x || this.rotationY != y || this.rotationZ != z) {
         this.rotationX = ValueUtil.CorrectInt(x, 0, 359);
         this.rotationY = ValueUtil.CorrectInt(y, 0, 359);
         this.rotationZ = ValueUtil.CorrectInt(z, 0, 359);
         this.needsClientUpdate = true;
      }
   }

   public void runScript(EnumScriptType type, Event event) {
      if (this.isEnabled()) {
         if (ScriptController.Instance.lastLoaded > this.lastInited) {
            this.lastInited = ScriptController.Instance.lastLoaded;
            if (type != EnumScriptType.INIT) {
               EventHooks.onScriptBlockInit(this);
            }
         }

         for(ScriptContainer script : this.scripts) {
            script.run(type, event);
         }

      }
   }

   public boolean isClient() {
      return this.getWorld().isRemote;
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean bo) {
      this.enabled = bo;
   }

   public String noticeString() {
      BlockPos pos = this.getPos();
      return MoreObjects.toStringHelper(this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
   }

   public String getLanguage() {
      return this.scriptLanguage;
   }

   public void setLanguage(String lang) {
      this.scriptLanguage = lang;
   }

   public List<ScriptContainer> getScripts() {
      return this.scripts;
   }

   public Map<Long, String> getConsoleText() {
      Map<Long, String> map = new TreeMap();
      int tab = 0;

      for(ScriptContainer script : this.getScripts()) {
         ++tab;

         for(Entry<Long, String> entry : script.console.entrySet()) {
            map.put(entry.getKey(), " tab " + tab + ":\n" + (String)entry.getValue());
         }
      }

      return map;
   }

   public void clearConsole() {
      for(ScriptContainer script : this.getScripts()) {
         script.console.clear();
      }

   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return Block.FULL_BLOCK_AABB.offset(this.getPos());
   }
}
