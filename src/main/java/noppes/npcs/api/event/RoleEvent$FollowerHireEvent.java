package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.ICustomNpc;

@Cancelable
public class RoleEvent$FollowerHireEvent extends RoleEvent {
   public int days;

   public RoleEvent$FollowerHireEvent(EntityPlayer player, ICustomNpc npc, int days) {
      super(player, npc);
      this.days = days;
   }
}
