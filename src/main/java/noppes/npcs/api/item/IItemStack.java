package noppes.npcs.api.item;

import net.minecraft.item.ItemStack;
import noppes.npcs.api.INbt;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.data.IData;

public interface IItemStack {
   int getStackSize();

   void setStackSize(int var1);

   int getMaxStackSize();

   int getItemDamage();

   void setItemDamage(int var1);

   int getMaxItemDamage();

   double getAttackDamage();

   void damageItem(int var1, IEntityLiving var2);

   boolean isEnchanted();

   boolean hasEnchant(int var1);

   /** @deprecated */
   boolean isBlock();

   boolean isWearable();

   boolean hasCustomName();

   void setCustomName(String var1);

   String getDisplayName();

   String getItemName();

   String getName();

   /** @deprecated */
   boolean isBook();

   IItemStack copy();

   ItemStack getMCItemStack();

   void addEnchantment(String var1, int var2);

   INbt getNbt();

   boolean hasNbt();

   INbt getItemNbt();

   boolean isEmpty();

   int getType();

   String[] getLore();

   void setLore(String[] var1);

   void setAttribute(String var1, double var2);

   double getAttribute(String var1);

   boolean hasAttribute(String var1);

   IData getTempdata();

   IData getStoreddata();

   int getFoodLevel();
}
