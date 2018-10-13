package noppes.npcs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomItems;
import noppes.npcs.items.ItemSoulstoneFilled;

class EntityNPCInterface$1 implements EntityProjectile$IProjectileCallback {
   // $FF: synthetic field
   final ItemStack val$proj;
   // $FF: synthetic field
   final EntityNPCInterface this$0;

   EntityNPCInterface$1(EntityNPCInterface this$0, ItemStack var2) {
      this.this$0 = this$0;
      this.val$proj = var2;
   }

   public boolean onImpact(EntityProjectile projectile, BlockPos pos, Entity entity) {
      if (this.val$proj.getItem() == CustomItems.soulstoneFull) {
         Entity e = ItemSoulstoneFilled.Spawn((EntityPlayer)null, this.val$proj, this.this$0.world, pos);
         if (e instanceof EntityLivingBase && entity instanceof EntityLivingBase) {
            if (e instanceof EntityLiving) {
               ((EntityLiving)e).setAttackTarget((EntityLivingBase)entity);
            } else {
               ((EntityLivingBase)e).setRevengeTarget((EntityLivingBase)entity);
            }
         }
      }

      projectile.playSound(this.this$0.stats.ranged.getSoundEvent(entity != null ? 1 : 2), 1.0F, 1.2F / (this.this$0.getRNG().nextFloat() * 0.2F + 0.9F));
      return false;
   }
}
