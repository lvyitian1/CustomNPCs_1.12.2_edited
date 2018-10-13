package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.IPlayerMail;

@Cancelable
public class RoleEvent$MailmanEvent extends RoleEvent {
   public final IPlayerMail mail;

   public RoleEvent$MailmanEvent(EntityPlayer player, ICustomNpc npc, IPlayerMail mail) {
      super(player, npc);
      this.mail = mail;
   }
}
