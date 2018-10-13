package noppes.npcs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateGround;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWaterNav extends EntityAIBase {
   private EntityNPCInterface entity;

   public EntityAIWaterNav(EntityNPCInterface par1EntityNPCInterface) {
      this.entity = par1EntityNPCInterface;
      ((PathNavigateGround)par1EntityNPCInterface.getNavigator()).setCanSwim(true);
   }

   public boolean shouldExecute() {
      if (this.entity.isInWater() || this.entity.isInLava()) {
         if (this.entity.ais.canSwim) {
            return true;
         }

         if (this.entity.collidedHorizontally) {
            return true;
         }
      }

      return false;
   }

   public void updateTask() {
      if (this.entity.getRNG().nextFloat() < 0.8F) {
         this.entity.getJumpHelper().setJumping();
      }

   }
}
