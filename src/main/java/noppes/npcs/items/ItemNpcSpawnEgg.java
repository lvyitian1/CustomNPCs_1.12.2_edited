package noppes.npcs.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryHandler;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import noppes.npcs.CreativeTabNpcs;
import noppes.npcs.CreativeTabSeparateNpcs;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.category.CategoryManager;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.controllers.LinkedNpcController$LinkedData;
import noppes.npcs.entity.EntityCustomNpc;

import java.util.Objects;
import java.util.Optional;

public class ItemNpcSpawnEgg extends Item {
    public LinkedNpcController$LinkedData data;
    public ItemNpcSpawnEgg(LinkedNpcController$LinkedData data){
        this.data=data;
        this.setTab(data.creativeInventory);
        this.setRegistryName("customnpcs","npc_"+this.data.name+"_spawnegg");
        this.setTranslationKey(this.data.name+"'s Spawn Egg");
        //((ForgeRegistry<Item>)(ForgeRegistries.ITEMS)).unfreeze();
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

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote)
        {
            EntityCustomNpc npc = new EntityCustomNpc(worldIn);
            npc.display.setName(this.data.name);
            npc.setPositionAndRotation(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
            npc.ais.setStartPos(pos);
            npc.linkedData=this.data;
            npc.linkedName=this.data.name;
            worldIn.spawnEntity(npc);
            npc.setHealth(npc.getMaxHealth());
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
