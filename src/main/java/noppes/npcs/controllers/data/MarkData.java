package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.Server;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.constants.EnumPacketClient;

public class MarkData implements ICapabilityProvider {
   @CapabilityInject(MarkData.class)
   public static Capability<MarkData> MARKDATA_CAPABILITY = null;
   private static final String NBTKEY = "cnpcmarkdata";
   private static final ResourceLocation CAPKEY = new ResourceLocation("customnpcs", "markdata");
   private EntityLivingBase entity;
   public List<MarkData$Mark> marks = new ArrayList();

   public void setNBT(NBTTagCompound compound) {
      List<MarkData$Mark> marks = new ArrayList();
      NBTTagList list = compound.getTagList("marks", 10);

      for(int i = 0; i < list.tagCount(); ++i) {
         NBTTagCompound c = list.getCompoundTagAt(i);
         MarkData$Mark m = new MarkData$Mark(this);
         m.type = c.getInteger("type");
         m.color = c.getInteger("color");
         m.availability.readFromNBT(c.getCompoundTag("availability"));
         marks.add(m);
      }

      this.marks = marks;
   }

   public NBTTagCompound getNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      NBTTagList list = new NBTTagList();

      for(MarkData$Mark m : this.marks) {
         NBTTagCompound c = new NBTTagCompound();
         c.setInteger("type", m.type);
         c.setInteger("color", m.color);
         c.setTag("availability", m.availability.writeToNBT(new NBTTagCompound()));
         list.appendTag(c);
      }

      compound.setTag("marks", list);
      return compound;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == MARKDATA_CAPABILITY;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(this.hasCapability(capability, facing) ? this : null);
   }

   public static void register(AttachCapabilitiesEvent<Entity> event) {
      event.addCapability(CAPKEY, new MarkData());
   }

   public void save() {
      this.entity.getEntityData().setTag("cnpcmarkdata", this.getNBT());
   }

   public IMark addMark(int type) {
      MarkData$Mark m = new MarkData$Mark(this);
      m.type = type;
      this.marks.add(m);
      if (!this.entity.world.isRemote) {
         this.syncClients();
      }

      return m;
   }

   public IMark addMark(int type, int color) {
      MarkData$Mark m = new MarkData$Mark(this);
      m.type = type;
      m.color = color;
      this.marks.add(m);
      if (!this.entity.world.isRemote) {
         this.syncClients();
      }

      return m;
   }

   public static MarkData get(EntityLivingBase entity) {
      MarkData data = (MarkData)entity.getCapability(MARKDATA_CAPABILITY, (EnumFacing)null);
      if (data.entity == null) {
         data.entity = entity;
         data.setNBT(entity.getEntityData().getCompoundTag("cnpcmarkdata"));
      }

      return data;
   }

   public void syncClients() {
      Server.sendToAll(this.entity.getServer(), EnumPacketClient.MARK_DATA, this.entity.getEntityId(), this.getNBT());
   }
}
