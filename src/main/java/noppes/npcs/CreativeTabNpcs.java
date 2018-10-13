package noppes.npcs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabNpcs extends CreativeTabs {
   public Item item = Items.BOWL;
   public int meta = 0;

   public CreativeTabNpcs(String label) {
      super(label);
   }

   public ItemStack createIcon() {
      return new ItemStack(this.item, 1, this.meta);
   }
}
