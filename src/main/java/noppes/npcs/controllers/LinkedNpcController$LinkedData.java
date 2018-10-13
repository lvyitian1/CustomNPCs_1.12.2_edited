package noppes.npcs.controllers;

import net.minecraft.nbt.NBTTagCompound;

public class LinkedNpcController$LinkedData {
   public String name = "LinkedNpc";
   public long time = 0L;
   public NBTTagCompound data = new NBTTagCompound();

   public LinkedNpcController$LinkedData() {
      this.time = System.currentTimeMillis();
   }

   public void setNBT(NBTTagCompound compound) {
      this.name = compound.getString("LinkedName");
      this.data = compound.getCompoundTag("NPCData");
   }

   public NBTTagCompound getNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("LinkedName", this.name);
      compound.setTag("NPCData", this.data);
      return compound;
   }
}
