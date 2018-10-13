package noppes.npcs.api.event;

import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.IPlayer;

public class PlayerEvent$KilledEntityEvent extends PlayerEvent {
   public final IEntityLivingBase entity;

   public PlayerEvent$KilledEntityEvent(IPlayer player, EntityLivingBase entity) {
      super(player);
      this.entity = (IEntityLivingBase)NpcAPI.Instance().getIEntity(entity);
   }
}
