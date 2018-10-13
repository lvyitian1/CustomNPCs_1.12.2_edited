package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.ICustomNpc;

@Cancelable
public class RoleEvent$TransporterUnlockedEvent extends RoleEvent {
   public RoleEvent$TransporterUnlockedEvent(EntityPlayer player, ICustomNpc npc) {
      super(player, npc);
   }
}
