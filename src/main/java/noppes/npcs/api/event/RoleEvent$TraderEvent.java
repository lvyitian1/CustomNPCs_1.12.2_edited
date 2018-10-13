package noppes.npcs.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.item.IItemStack;

@Cancelable
public class RoleEvent$TraderEvent extends RoleEvent {
   public IItemStack sold;
   public IItemStack currency1;
   public IItemStack currency2;

   public RoleEvent$TraderEvent(EntityPlayer player, ICustomNpc npc, ItemStack sold, ItemStack currency1, ItemStack currency2) {
      super(player, npc);
      this.currency1 = currency1.isEmpty() ? null : NpcAPI.Instance().getIItemStack(currency1.copy());
      this.currency2 = currency2.isEmpty() ? null : NpcAPI.Instance().getIItemStack(currency2.copy());
      this.sold = NpcAPI.Instance().getIItemStack(sold.copy());
   }
}
