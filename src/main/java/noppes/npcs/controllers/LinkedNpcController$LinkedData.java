package noppes.npcs.controllers;

import net.minecraft.nbt.NBTTagCompound;

public class LinkedNpcController$LinkedData {
   public static enum ShowInCreativeInventory{
      MOD,
      VANILLA,
      SEPARATE,
      NONE
   }
   public String name = "LinkedNpc";
   public long time = 0L;
   public ShowInCreativeInventory creativeInventory=ShowInCreativeInventory.NONE;
   //public String iconFile="";
   public NBTTagCompound data = new NBTTagCompound();

   public LinkedNpcController$LinkedData() {
      this.time = System.currentTimeMillis();
   }

   public void setNBT(NBTTagCompound compound) {
      this.name = compound.getString("LinkedName");
      this.data = compound.getCompoundTag("NPCData");
      this.creativeInventory = ShowInCreativeInventory.values()[compound.getInteger("creativeInventory")];
      //this.iconFile=compound.getString("iconFile");
   }

   public NBTTagCompound getNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("LinkedName", this.name);
      compound.setTag("NPCData", this.data);
      compound.setInteger("creativeInventory",this.creativeInventory.ordinal());
      //compound.setString("iconFile",this.iconFile);
      return compound;
   }
}
