package noppes.npcs.roles;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.data.role.IJobPuppet$IJobPuppetPart;
import noppes.npcs.util.ValueUtil;

public class JobPuppet$PartConfig implements IJobPuppet$IJobPuppetPart {
   public float rotationX;
   public float rotationY;
   public float rotationZ;
   public boolean disabled;
   // $FF: synthetic field
   final JobPuppet this$0;

   public JobPuppet$PartConfig(JobPuppet this$0) {
      this.this$0 = this$0;
      this.rotationX = 0.0F;
      this.rotationY = 0.0F;
      this.rotationZ = 0.0F;
      this.disabled = false;
   }

   public NBTTagCompound writeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setFloat("RotationX", this.rotationX);
      compound.setFloat("RotationY", this.rotationY);
      compound.setFloat("RotationZ", this.rotationZ);
      compound.setBoolean("Disabled", this.disabled);
      return compound;
   }

   public void readNBT(NBTTagCompound compound) {
      this.rotationX = ValueUtil.correctFloat(compound.getFloat("RotationX"), -1.0F, 1.0F);
      this.rotationY = ValueUtil.correctFloat(compound.getFloat("RotationY"), -1.0F, 1.0F);
      this.rotationZ = ValueUtil.correctFloat(compound.getFloat("RotationZ"), -1.0F, 1.0F);
      this.disabled = compound.getBoolean("Disabled");
   }

   public int getRotationX() {
      return (int)(this.rotationX + 180.0F);
   }

   public int getRotationY() {
      return (int)(this.rotationY + 180.0F);
   }

   public int getRotationZ() {
      return (int)(this.rotationZ + 180.0F);
   }

   public void setRotation(int x, int y, int z) {
      this.disabled = false;
      this.rotationX = ValueUtil.correctFloat((float)x / 180.0F - 1.0F, -1.0F, 1.0F);
      this.rotationY = ValueUtil.correctFloat((float)y / 180.0F - 1.0F, -1.0F, 1.0F);
      this.rotationZ = ValueUtil.correctFloat((float)z / 180.0F - 1.0F, -1.0F, 1.0F);
      this.this$0.npc.updateClient = true;
   }
}
