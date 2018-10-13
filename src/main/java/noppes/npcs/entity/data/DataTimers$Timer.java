package noppes.npcs.entity.data;

import noppes.npcs.EventHooks;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

class DataTimers$Timer {
   public int id;
   private boolean repeat;
   private int timerTicks;
   private int ticks;
   // $FF: synthetic field
   final DataTimers this$0;

   public DataTimers$Timer(DataTimers this$0, int id, int ticks, boolean repeat) {
      this.this$0 = this$0;
      this.ticks = 0;
      this.id = id;
      this.repeat = repeat;
      this.timerTicks = ticks;
      this.ticks = ticks;
   }

   public void update() {
      if (this.ticks-- <= 0) {
         if (this.repeat) {
            this.ticks = this.timerTicks;
         } else {
            this.this$0.stop(this.id);
         }

         Object ob = DataTimers.access$200(this.this$0);
         if (ob instanceof EntityNPCInterface) {
            EventHooks.onNPCTimer((EntityNPCInterface)ob, this.id);
         } else if (ob instanceof PlayerData) {
            EventHooks.onPlayerTimer((PlayerData)ob, this.id);
         } else {
            EventHooks.onScriptBlockTimer((IScriptBlockHandler)ob, this.id);
         }

      }
   }

   // $FF: synthetic method
   static int access$002(DataTimers$Timer x0, int x1) {
      return x0.ticks = x1;
   }

   // $FF: synthetic method
   static boolean access$100(DataTimers$Timer x0) {
      return x0.repeat;
   }

   // $FF: synthetic method
   static int access$000(DataTimers$Timer x0) {
      return x0.ticks;
   }
}
