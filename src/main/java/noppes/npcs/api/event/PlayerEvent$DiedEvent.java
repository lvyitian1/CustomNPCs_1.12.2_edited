package noppes.npcs.api.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;

@Cancelable
public class PlayerEvent$DiedEvent extends PlayerEvent {
   public final IDamageSource damageSource;
   public final String type;
   public final IEntity source;

   public PlayerEvent$DiedEvent(IPlayer player, DamageSource damagesource, Entity entity) {
      super(player);
      this.damageSource = NpcAPI.Instance().getIDamageSource(damagesource);
      this.type = damagesource.damageType;
      this.source = NpcAPI.Instance().getIEntity(entity);
   }
}
