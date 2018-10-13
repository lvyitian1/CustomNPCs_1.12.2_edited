package noppes.npcs.client.gui;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.schematics.ISchematic;

class GuiBlockBuilder$1 implements ISchematic {
   // $FF: synthetic field
   final NBTTagCompound val$compound;
   // $FF: synthetic field
   final List val$states;
   // $FF: synthetic field
   final GuiBlockBuilder this$0;

   GuiBlockBuilder$1(GuiBlockBuilder this$0, NBTTagCompound var2, List var3) {
      this.this$0 = this$0;
      this.val$compound = var2;
      this.val$states = var3;
   }

   public short getWidth() {
      return this.val$compound.getShort("Width");
   }

   public int getTileEntitySize() {
      return 0;
   }

   public NBTTagCompound getTileEntity(int i) {
      return null;
   }

   public String getName() {
      return this.val$compound.getString("SchematicName");
   }

   public short getLength() {
      return this.val$compound.getShort("Length");
   }

   public short getHeight() {
      return this.val$compound.getShort("Height");
   }

   public IBlockState getBlockState(int i) {
      return (IBlockState)this.val$states.get(i);
   }

   public IBlockState getBlockState(int x, int y, int z) {
      return this.getBlockState((y * this.getLength() + z) * this.getWidth() + x);
   }

   public NBTTagCompound getNBT() {
      return null;
   }
}
