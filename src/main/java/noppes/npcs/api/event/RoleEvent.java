package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;

public class RoleEvent extends CustomNPCsEvent {
   public final ICustomNpc npc;
   public final IPlayer player;

   public RoleEvent(EntityPlayer player, ICustomNpc npc) {
      this.npc = npc;
      this.player = (IPlayer)NpcAPI.Instance().getIEntity(player);
   }
}
