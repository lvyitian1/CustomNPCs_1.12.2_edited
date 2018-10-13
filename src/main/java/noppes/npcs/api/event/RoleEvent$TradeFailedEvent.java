package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.item.IItemStack;

public class RoleEvent$TradeFailedEvent extends RoleEvent {
   public final IItemStack sold;
   public final IItemStack currency1;
   public final IItemStack currency2;
   public IItemStack receiving;

   public RoleEvent$TradeFailedEvent(EntityPlayer player, ICustomNpc npc, ItemStack sold, ItemStack currency1, ItemStack currency2) {
      super(player, npc);
      this.currency1 = currency1.isEmpty() ? null : NpcAPI.Instance().getIItemStack(currency1.copy());
      this.currency2 = currency2.isEmpty() ? null : NpcAPI.Instance().getIItemStack(currency2.copy());
      this.sold = NpcAPI.Instance().getIItemStack(sold.copy());
   }
}
