package noppes.npcs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabSeparateNpcs extends CreativeTabs {
        public static final CreativeTabSeparateNpcs INSTANCE=new CreativeTabSeparateNpcs("separate_npcs");

        public Item item = Items.BOWL;
        public int meta = 0;

        private CreativeTabSeparateNpcs(String label) {
            super(label);
        }

        public ItemStack createIcon() {
            return new ItemStack(this.item, 1, this.meta);
        }
}
