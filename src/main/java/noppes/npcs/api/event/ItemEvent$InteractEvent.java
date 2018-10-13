package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemScripted;

@Cancelable
public class ItemEvent$InteractEvent extends ItemEvent {
   public final int type;
   public final Object target;
   public IPlayer player;

   public ItemEvent$InteractEvent(IItemScripted item, IPlayer player, int type, Object target) {
      super(item);
      this.type = type;
      this.target = target;
      this.player = player;
   }
}
