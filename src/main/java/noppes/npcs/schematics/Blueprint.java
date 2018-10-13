package noppes.npcs.schematics;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Blueprint implements ISchematic {
   private List<String> requiredMods;
   private short sizeX;
   private short sizeY;
   private short sizeZ;
   private short palleteSize;
   private IBlockState[] pallete;
   private String name;
   private String[] architects;
   private short[][][] structure;
   private NBTTagCompound[] tileEntities;

   public Blueprint(short sizeX, short sizeY, short sizeZ, short palleteSize, IBlockState[] pallete, short[][][] structure, NBTTagCompound[] tileEntities, List<String> requiredMods) {
      this.sizeX = sizeX;
      this.sizeY = sizeY;
      this.sizeZ = sizeZ;
      this.palleteSize = palleteSize;
      this.pallete = pallete;
      this.structure = structure;
      this.tileEntities = tileEntities;
      this.requiredMods = requiredMods;
   }

   public void build(World world, BlockPos pos) {
      IBlockState[] pallete = this.getPallete();
      short[][][] structure = this.getStructure();

      for(short y = 0; y < this.getSizeY(); ++y) {
         for(short z = 0; z < this.getSizeZ(); ++z) {
            for(short x = 0; x < this.getSizeX(); ++x) {
               IBlockState state = pallete[structure[y][z][x] & '\uffff'];
               if (state.getBlock() != Blocks.STRUCTURE_VOID && state.isFullCube()) {
                  world.setBlockState(pos.add(x, y, z), state, 2);
               }
            }
         }
      }

      for(short y = 0; y < this.getSizeY(); ++y) {
         for(short z = 0; z < this.getSizeZ(); ++z) {
            for(short x = 0; x < this.getSizeX(); ++x) {
               IBlockState state = pallete[structure[y][z][x]];
               if (state.getBlock() != Blocks.STRUCTURE_VOID && !state.isFullCube()) {
                  world.setBlockState(pos.add(x, y, z), state, 2);
               }
            }
         }
      }

      if (this.getTileEntities() != null) {
         for(NBTTagCompound tag : this.getTileEntities()) {
            TileEntity te = world.getTileEntity(pos.add(tag.getShort("x"), tag.getShort("y"), tag.getShort("z")));
            tag.setInteger("x", pos.getX() + tag.getShort("x"));
            tag.setInteger("y", pos.getY() + tag.getShort("y"));
            tag.setInteger("z", pos.getZ() + tag.getShort("z"));
            te.deserializeNBT(tag);
         }
      }

   }

   public short getSizeX() {
      return this.sizeX;
   }

   public short getSizeY() {
      return this.sizeY;
   }

   public short getSizeZ() {
      return this.sizeZ;
   }

   public short getPalleteSize() {
      return this.palleteSize;
   }

   public IBlockState[] getPallete() {
      return this.pallete;
   }

   public short[][][] getStructure() {
      return this.structure;
   }

   public NBTTagCompound[] getTileEntities() {
      return this.tileEntities;
   }

   public List<String> getRequiredMods() {
      return this.requiredMods;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String[] getArchitects() {
      return this.architects;
   }

   public void setArchitects(String[] architects) {
      this.architects = architects;
   }

   public short getWidth() {
      return this.getSizeX();
   }

   public short getHeight() {
      return this.getSizeZ();
   }

   public short getLength() {
      return this.getSizeY();
   }

   public int getTileEntitySize() {
      return this.tileEntities.length;
   }

   public NBTTagCompound getTileEntity(int i) {
      return this.tileEntities[i];
   }

   public IBlockState getBlockState(int x, int y, int z) {
      return this.pallete[this.structure[y][z][x]];
   }

   public IBlockState getBlockState(int i) {
      int x = i % this.getWidth();
      int z = (i - x) / this.getWidth() % this.getLength();
      int y = ((i - x) / this.getWidth() - z) / this.getLength();
      return this.getBlockState(x, y, z);
   }

   public NBTTagCompound getNBT() {
      return BlueprintUtil.writeBlueprintToNBT(this);
   }
}
