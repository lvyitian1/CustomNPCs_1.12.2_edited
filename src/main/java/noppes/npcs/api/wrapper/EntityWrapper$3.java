package noppes.npcs.api.wrapper;

import java.util.Comparator;
import noppes.npcs.api.entity.IEntity;

class EntityWrapper$3 implements Comparator<IEntity> {
   // $FF: synthetic field
   final EntityWrapper this$0;

   EntityWrapper$3(EntityWrapper this$0) {
      this.this$0 = this$0;
   }

   @Override
   public int compare(IEntity o1, IEntity o2) {
      double d1 = this.this$0.entity.getDistanceSq(o1.getMCEntity());
      double d2 = this.this$0.entity.getDistanceSq(o2.getMCEntity());
      if (d1 == d2) {
         return 0;
      } else {
         return d1 > d2 ? 1 : -1;
      }
   }

}
