package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.ITimers;

public class DataTimers implements ITimers {
   private Object parent;
   private Map<Integer, DataTimers$Timer> timers = new HashMap();

   public DataTimers(Object parent) {
      this.parent = parent;
   }

   public void start(int id, int ticks, boolean repeat) {
      if (this.timers.containsKey(Integer.valueOf(id))) {
         throw new CustomNPCsException("There is already a timer with id: " + id, new Object[0]);
      } else {
         this.timers.put(Integer.valueOf(id), new DataTimers$Timer(this, id, ticks, repeat));
      }
   }

   public void forceStart(int id, int ticks, boolean repeat) {
      this.timers.put(Integer.valueOf(id), new DataTimers$Timer(this, id, ticks, repeat));
   }

   public boolean has(int id) {
      return this.timers.containsKey(Integer.valueOf(id));
   }

   public boolean stop(int id) {
      return this.timers.remove(Integer.valueOf(id)) != null;
   }

   public void reset(int id) {
      DataTimers$Timer timer = (DataTimers$Timer)this.timers.get(Integer.valueOf(id));
      if (timer == null) {
         throw new CustomNPCsException("There is no timer with id: " + id, new Object[0]);
      } else {
         DataTimers$Timer.access$002(timer, 0);
      }
   }

   public void writeToNBT(NBTTagCompound compound) {
      NBTTagList list = new NBTTagList();

      for(DataTimers$Timer timer : this.timers.values()) {
         NBTTagCompound c = new NBTTagCompound();
         c.setInteger("ID", timer.id);
         c.setInteger("TimerTicks", timer.id);
         c.setBoolean("Repeat", DataTimers$Timer.access$100(timer));
         c.setInteger("Ticks", DataTimers$Timer.access$000(timer));
         list.appendTag(c);
      }

      compound.setTag("NpcsTimers", list);
   }

   public void readFromNBT(NBTTagCompound compound) {
      Map<Integer, DataTimers$Timer> timers = new HashMap();
      NBTTagList list = compound.getTagList("NpcsTimers", 10);

      for(int i = 0; i < list.tagCount(); ++i) {
         NBTTagCompound c = list.getCompoundTagAt(i);
         DataTimers$Timer t = new DataTimers$Timer(this, c.getInteger("ID"), c.getInteger("TimerTicks"), c.getBoolean("Repeat"));
         DataTimers$Timer.access$002(t, c.getInteger("Ticks"));
         timers.put(Integer.valueOf(t.id), t);
      }

      this.timers = timers;
   }

   public void update() {
      for(DataTimers$Timer timer : this.timers.values()) {
         timer.update();
      }

   }

   public void clear() {
      this.timers = new HashMap();
   }

   // $FF: synthetic method
   static Object access$200(DataTimers x0) {
      return x0.parent;
   }
}
