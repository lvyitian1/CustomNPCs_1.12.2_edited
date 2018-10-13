package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogOption;

@Cancelable
public class DialogEvent$OptionEvent extends DialogEvent {
   public final IDialogOption option;

   public DialogEvent$OptionEvent(ICustomNpc npc, EntityPlayer player, IDialog dialog, IDialogOption option) {
      super(npc, player, dialog);
      this.option = option;
   }
}
