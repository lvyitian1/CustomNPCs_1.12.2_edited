package noppes.npcs;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.items.ItemSoulstoneFilled;

class CustomItems$1 extends BehaviorDefaultDispenseItem {
   // $FF: synthetic field
   final CustomItems this$0;

   CustomItems$1(CustomItems this$0) {
      this.this$0 = this$0;
   }

   public ItemStack dispenseStack(IBlockSource source, ItemStack item) {
      EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue(BlockDispenser.FACING);
      double x = source.getX() + (double)enumfacing.getXOffset();
      double z = source.getZ() + (double)enumfacing.getZOffset();
      ItemSoulstoneFilled.Spawn((EntityPlayer)null, item, source.getWorld(), new BlockPos(x, source.getY(), z));
      item.splitStack(1);
      return item;
   }
}
