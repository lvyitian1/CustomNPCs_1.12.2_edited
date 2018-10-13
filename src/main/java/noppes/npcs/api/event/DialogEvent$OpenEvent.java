package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.handler.data.IDialog;

@Cancelable
public class DialogEvent$OpenEvent extends DialogEvent {
   public DialogEvent$OpenEvent(ICustomNpc npc, EntityPlayer player, IDialog dialog) {
      super(npc, player, dialog);
   }
}
