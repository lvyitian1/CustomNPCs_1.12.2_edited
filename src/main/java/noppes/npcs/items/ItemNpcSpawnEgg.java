package noppes.npcs.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryHandler;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.CreativeTabNpcs;
import noppes.npcs.CreativeTabSeparateNpcs;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.category.CategoryManager;
import noppes.npcs.controllers.LinkedNpcController$LinkedData;

import java.util.Objects;
import java.util.Optional;

public class ItemNpcSpawnEgg extends Item {
    public LinkedNpcController$LinkedData data;
    public ItemNpcSpawnEgg(LinkedNpcController$LinkedData data){
        this.data=data;
        this.setTab(data.creativeInventory);
        this.setRegistryName("customnpcs","npc_"+this.data.name+"_spawnegg");
        this.setTranslationKey(this.data.name+"'s Spawn Egg");
        ForgeRegistries.ITEMS.register(this);
        ModelLoader.setCustomModelResourceLocation(this,0,new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()),"inventory"));
    }
    public void setTab(LinkedNpcController$LinkedData.ShowInCreativeInventory tab){
        switch(tab){
            case NONE: this.setCreativeTab(null);break;
            case MOD: this.setCreativeTab(CustomItems.tab); break;
            case VANILLA: this.setCreativeTab(CreativeTabs.MISC); break;
            case SEPARATE: this.setCreativeTab(CreativeTabSeparateNpcs.INSTANCE); break;
        }
        CategoryManager.INSTANCE.tabs.put(this, Optional.ofNullable(this.getCreativeTab()));
    }

}
