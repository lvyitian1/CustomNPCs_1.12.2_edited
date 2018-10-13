package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.ICustomNpc;

public class RoleEvent$FollowerFinishedEvent extends RoleEvent {
   public RoleEvent$FollowerFinishedEvent(EntityPlayer player, ICustomNpc npc) {
      super(player, npc);
   }
}
