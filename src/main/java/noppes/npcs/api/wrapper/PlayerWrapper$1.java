package noppes.npcs.api.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IPixelmon;
import noppes.npcs.api.entity.data.IPixelmonPlayerData;
import noppes.npcs.controllers.PixelmonHelper;

class PlayerWrapper$1 implements IPixelmonPlayerData {
   // $FF: synthetic field
   final PlayerWrapper this$0;

   PlayerWrapper$1(PlayerWrapper this$0) {
      this.this$0 = this$0;
   }

   public IPixelmon getPartySlot(int slot) {
      NBTTagCompound compound = PixelmonHelper.getPartySlot(slot, (EntityPlayer)this.this$0.entity);
      return compound == null ? null : (IPixelmon)NpcAPI.Instance().getIEntity(PixelmonHelper.pixelmonFromNBT(compound, (EntityPlayer)this.this$0.entity));
   }

   public int countPCPixelmon() {
      return PixelmonHelper.countPCPixelmon((EntityPlayerMP)this.this$0.entity);
   }
}
