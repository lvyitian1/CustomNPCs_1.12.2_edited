package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemStack;

@Cancelable
public class PlayerEvent$PickUpEvent extends PlayerEvent {
   public final IItemStack item;

   public PlayerEvent$PickUpEvent(IPlayer player, IItemStack item) {
      super(player);
      this.item = item;
   }
}
