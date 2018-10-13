package noppes.npcs.controllers.data;

import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.handler.data.IAvailability;

public class MarkData$Mark implements IMark {
   public int type;
   public Availability availability;
   public int color;
   // $FF: synthetic field
   final MarkData this$0;

   public MarkData$Mark(MarkData this$0) {
      this.this$0 = this$0;
      this.type = 0;
      this.availability = new Availability();
      this.color = 16772433;
   }

   public IAvailability getAvailability() {
      return this.availability;
   }

   public int getColor() {
      return this.color;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public void update() {
      this.this$0.syncClients();
   }
}
