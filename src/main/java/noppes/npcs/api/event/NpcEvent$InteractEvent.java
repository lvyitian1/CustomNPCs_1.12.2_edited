package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;

@Cancelable
public class NpcEvent$InteractEvent extends NpcEvent {
   public final IPlayer player;

   public NpcEvent$InteractEvent(ICustomNpc npc, EntityPlayer player) {
      super(npc);
      this.player = (IPlayer)NpcAPI.Instance().getIEntity(player);
   }
}
