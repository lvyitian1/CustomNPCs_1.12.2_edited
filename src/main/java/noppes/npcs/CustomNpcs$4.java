package noppes.npcs;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import noppes.npcs.api.wrapper.ItemStackWrapper;

class CustomNpcs$4 implements IStorage<ItemStackWrapper> {
   // $FF: synthetic field
   final CustomNpcs this$0;

   CustomNpcs$4(CustomNpcs this$0) {
      this.this$0 = this$0;
   }

   @Override
   public NBTBase writeNBT(Capability capability, ItemStackWrapper instance, EnumFacing side) {
      return null;
   }

   @Override
   public void readNBT(Capability capability, ItemStackWrapper instance, EnumFacing side, NBTBase nbt) {
   }
}
