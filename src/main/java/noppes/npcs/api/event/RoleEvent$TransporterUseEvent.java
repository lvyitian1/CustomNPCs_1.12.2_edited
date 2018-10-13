package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.role.IRoleTransporter$ITransportLocation;

@Cancelable
public class RoleEvent$TransporterUseEvent extends RoleEvent {
   public final IRoleTransporter$ITransportLocation location;

   public RoleEvent$TransporterUseEvent(EntityPlayer player, ICustomNpc npc, IRoleTransporter$ITransportLocation location) {
      super(player, npc);
      this.location = location;
   }
}
