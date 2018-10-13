package noppes.npcs.api.event;

import noppes.npcs.api.item.IItemScripted;

public class ItemEvent extends CustomNPCsEvent {
   public IItemScripted item;

   public ItemEvent(IItemScripted item) {
      this.item = item;
   }
}
