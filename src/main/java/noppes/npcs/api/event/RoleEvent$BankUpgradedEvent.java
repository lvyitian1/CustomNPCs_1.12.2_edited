package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.ICustomNpc;

public class RoleEvent$BankUpgradedEvent extends RoleEvent {
   public final int slot;

   public RoleEvent$BankUpgradedEvent(EntityPlayer player, ICustomNpc npc, int slot) {
      super(player, npc);
      this.slot = slot;
   }
}
