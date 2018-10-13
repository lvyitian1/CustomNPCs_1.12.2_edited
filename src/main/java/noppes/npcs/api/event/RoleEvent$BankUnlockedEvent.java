package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.ICustomNpc;

public class RoleEvent$BankUnlockedEvent extends RoleEvent {
   public final int slot;

   public RoleEvent$BankUnlockedEvent(EntityPlayer player, ICustomNpc npc, int slot) {
      super(player, npc);
      this.slot = slot;
   }
}
