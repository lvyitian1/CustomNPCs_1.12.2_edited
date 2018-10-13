package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.handler.data.IDialog;

public class DialogEvent$CloseEvent extends DialogEvent {
   public DialogEvent$CloseEvent(ICustomNpc npc, EntityPlayer player, IDialog dialog) {
      super(npc, player, dialog);
   }
}
