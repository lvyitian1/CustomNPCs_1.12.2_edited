package noppes.npcs.category;

import net.minecraft.creativetab.CreativeTabs;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.controllers.LinkedNpcController$LinkedData;
import noppes.npcs.items.ItemNpcSpawnEgg;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CategoryManager {
    public static final CategoryManager INSTANCE=new CategoryManager();
    private CategoryManager(){}
    public ConcurrentHashMap<ItemNpcSpawnEgg, Optional<CreativeTabs>> tabs=new ConcurrentHashMap<>();
    public void reload(){
        LinkedNpcController.Instance.list.forEach(ItemNpcSpawnEgg::new);
    }
    public void setCreativeInventory(LinkedNpcController$LinkedData data, LinkedNpcController$LinkedData.ShowInCreativeInventory inv)
    {
        data.creativeInventory=inv;
        tabs.keySet().parallelStream().filter(i-> Objects.equals(i.data.name,data.name)).findFirst().ifPresent(i->i.setTab(inv));
    }
}
