package noppes.npcs.controllers.data;

import java.util.HashMap;
import java.util.Vector;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TransportCategory {
   public int id = -1;
   public String title = "";
   public HashMap<Integer, TransportLocation> locations = new HashMap();

   public Vector<TransportLocation> getDefaultLocations() {
      Vector<TransportLocation> list = new Vector();

      for(TransportLocation loc : this.locations.values()) {
         if (loc.isDefault()) {
            list.add(loc);
         }
      }

      return list;
   }

   public void readNBT(NBTTagCompound compound) {
      this.id = compound.getInteger("CategoryId");
      this.title = compound.getString("CategoryTitle");
      NBTTagList locs = compound.getTagList("CategoryLocations", 10);
      if (locs != null && locs.tagCount() != 0) {
         for(int ii = 0; ii < locs.tagCount(); ++ii) {
            TransportLocation location = new TransportLocation();
            location.readNBT(locs.getCompoundTagAt(ii));
            location.category = this;
            this.locations.put(Integer.valueOf(location.id), location);
         }

      }
   }

   public void writeNBT(NBTTagCompound compound) {
      compound.setInteger("CategoryId", this.id);
      compound.setString("CategoryTitle", this.title);
      NBTTagList locs = new NBTTagList();

      for(TransportLocation location : this.locations.values()) {
         locs.appendTag(location.writeNBT());
      }

      compound.setTag("CategoryLocations", locs);
   }
}
