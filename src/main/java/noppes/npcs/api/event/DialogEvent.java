package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IDialog;

public class DialogEvent extends NpcEvent {
   public final IDialog dialog;
   public final IPlayer player;

   public DialogEvent(ICustomNpc npc, EntityPlayer player, IDialog dialog) {
      super(npc);
      this.dialog = dialog;
      this.player = (IPlayer)NpcAPI.Instance().getIEntity(player);
   }
}
