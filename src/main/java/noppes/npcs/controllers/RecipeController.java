package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.Server;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.data.RecipesDefault;

public class RecipeController implements IRecipeHandler {
   public HashMap<Integer, RecipeCarpentry> globalRecipes = new HashMap();
   public HashMap<Integer, RecipeCarpentry> anvilRecipes = new HashMap();
   public static RecipeController instance;
   public static final int version = 1;
   public int nextId = 1;
   public static HashMap<Integer, RecipeCarpentry> syncRecipes = new HashMap();
   public static IForgeRegistry<IRecipe> Registry;

   public RecipeController() {
      instance = this;
   }

   public void load() {
      this.loadCategories();
      this.reloadGlobalRecipes();
      EventHooks.onGlobalRecipesLoaded(this);
   }

   public void reloadGlobalRecipes() {
   }

   private void loadCategories() {
      File saveDir = CustomNpcs.getWorldSaveDirectory();

      try {
         File file = new File(saveDir, "recipes.dat");
         if (file.exists()) {
            this.loadCategories(file);
         } else {
            this.globalRecipes.clear();
            this.anvilRecipes.clear();
            this.loadDefaultRecipes(-1);
         }
      } catch (Exception var5) {
         var5.printStackTrace();

         try {
            File file = new File(saveDir, "recipes.dat_old");
            if (file.exists()) {
               this.loadCategories(file);
            }
         } catch (Exception var4) {
            var5.printStackTrace();
         }
      }

   }

   private void loadDefaultRecipes(int i) {
      if (i != 1) {
         RecipesDefault.loadDefaultRecipes(i);
         this.saveCategories();
      }
   }

   private void loadCategories(File file) throws Exception {
      NBTTagCompound nbttagcompound1 = CompressedStreamTools.readCompressed(new FileInputStream(file));
      this.nextId = nbttagcompound1.getInteger("LastId");
      NBTTagList list = nbttagcompound1.getTagList("Data", 10);
      HashMap<Integer, RecipeCarpentry> globalRecipes = new HashMap();
      HashMap<Integer, RecipeCarpentry> anvilRecipes = new HashMap();
      if (list != null) {
         for(int i = 0; i < list.tagCount(); ++i) {
            RecipeCarpentry recipe = RecipeCarpentry.read(list.getCompoundTagAt(i));
            if (recipe.isGlobal) {
               globalRecipes.put(Integer.valueOf(recipe.id), recipe);
            } else {
               anvilRecipes.put(Integer.valueOf(recipe.id), recipe);
            }

            if (recipe.id > this.nextId) {
               this.nextId = recipe.id;
            }
         }
      }

      this.anvilRecipes = anvilRecipes;
      this.globalRecipes = globalRecipes;
      this.loadDefaultRecipes(nbttagcompound1.getInteger("Version"));
   }

   private void saveCategories() {
      try {
         File saveDir = CustomNpcs.getWorldSaveDirectory();
         NBTTagList list = new NBTTagList();

         for(RecipeCarpentry recipe : this.globalRecipes.values()) {
            if (recipe.savesRecipe) {
               list.appendTag(recipe.writeNBT());
            }
         }

         for(RecipeCarpentry recipe : this.anvilRecipes.values()) {
            if (recipe.savesRecipe) {
               list.appendTag(recipe.writeNBT());
            }
         }

         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setTag("Data", list);
         nbttagcompound.setInteger("LastId", this.nextId);
         nbttagcompound.setInteger("Version", 1);
         File file = new File(saveDir, "recipes.dat_new");
         File file1 = new File(saveDir, "recipes.dat_old");
         File file2 = new File(saveDir, "recipes.dat");
         CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file));
         if (file1.exists()) {
            file1.delete();
         }

         file2.renameTo(file1);
         if (file2.exists()) {
            file2.delete();
         }

         file.renameTo(file2);
         if (file.exists()) {
            file.delete();
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public RecipeCarpentry findMatchingRecipe(InventoryCrafting par1InventoryCrafting) {
      for(RecipeCarpentry recipe : this.anvilRecipes.values()) {
         if (recipe.isValid() && recipe.matches(par1InventoryCrafting, (World)null)) {
            return recipe;
         }
      }

      return null;
   }

   public RecipeCarpentry getRecipe(int id) {
      if (this.globalRecipes.containsKey(Integer.valueOf(id))) {
         return (RecipeCarpentry)this.globalRecipes.get(Integer.valueOf(id));
      } else {
         return this.anvilRecipes.containsKey(Integer.valueOf(id)) ? (RecipeCarpentry)this.anvilRecipes.get(Integer.valueOf(id)) : null;
      }
   }

   public RecipeCarpentry saveRecipe(RecipeCarpentry recipe) throws IOException {
      RecipeCarpentry current = this.getRecipe(recipe.id);
      if (current != null && !current.name.equals(recipe.name)) {
         while(this.containsRecipeName(recipe.name)) {
            recipe.name = recipe.name + "_";
         }
      }

      if (recipe.id == -1) {
         for(recipe.id = this.getUniqueId(); this.containsRecipeName(recipe.name); recipe.name = recipe.name + "_") {
            ;
         }
      }

      if (recipe.isGlobal) {
         this.anvilRecipes.remove(Integer.valueOf(recipe.id));
         this.globalRecipes.put(Integer.valueOf(recipe.id), recipe);
         Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, Integer.valueOf(6), recipe.writeNBT());
      } else {
         this.globalRecipes.remove(Integer.valueOf(recipe.id));
         this.anvilRecipes.put(Integer.valueOf(recipe.id), recipe);
         Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_UPDATE, Integer.valueOf(7), recipe.writeNBT());
      }

      this.saveCategories();
      this.reloadGlobalRecipes();
      return recipe;
   }

   private int getUniqueId() {
      ++this.nextId;
      return this.nextId;
   }

   private boolean containsRecipeName(String name) {
      name = name.toLowerCase();

      for(RecipeCarpentry recipe : this.globalRecipes.values()) {
         if (recipe.name.toLowerCase().equals(name)) {
            return true;
         }
      }

      for(RecipeCarpentry recipe : this.anvilRecipes.values()) {
         if (recipe.name.toLowerCase().equals(name)) {
            return true;
         }
      }

      return false;
   }

   public RecipeCarpentry delete(int id) {
      RecipeCarpentry recipe = this.getRecipe(id);
      if (recipe == null) {
         return null;
      } else {
         this.globalRecipes.remove(Integer.valueOf(recipe.id));
         this.anvilRecipes.remove(Integer.valueOf(recipe.id));
         if (recipe.isGlobal) {
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, Integer.valueOf(6), id);
         } else {
            Server.sendToAll(CustomNpcs.Server, EnumPacketClient.SYNC_REMOVE, Integer.valueOf(7), id);
         }

         this.saveCategories();
         this.reloadGlobalRecipes();
         recipe.id = -1;
         return recipe;
      }
   }

   public List<noppes.npcs.api.handler.data.IRecipe> getGlobalList() {
      return new ArrayList(this.globalRecipes.values());
   }

   public List<noppes.npcs.api.handler.data.IRecipe> getCarpentryList() {
      return new ArrayList(this.anvilRecipes.values());
   }

   public noppes.npcs.api.handler.data.IRecipe addRecipe(String name, boolean global, ItemStack result, Object... objects) {
      RecipeCarpentry recipe = new RecipeCarpentry(name);
      recipe.isGlobal = global;
      recipe = RecipeCarpentry.createRecipe(recipe, result, objects);

      try {
         return this.saveRecipe(recipe);
      } catch (IOException var7) {
         var7.printStackTrace();
         return recipe;
      }
   }

   public noppes.npcs.api.handler.data.IRecipe addRecipe(String name, boolean global, ItemStack result, int width, int height, ItemStack... objects) {
      NonNullList<Ingredient> list = NonNullList.create();

      for(ItemStack item : objects) {
         if (!item.isEmpty()) {
            list.add(Ingredient.fromStacks(new ItemStack[]{item}));
         }
      }

      RecipeCarpentry recipe = new RecipeCarpentry(width, height, list, result);
      recipe.isGlobal = global;
      recipe.name = name;

      try {
         return this.saveRecipe(recipe);
      } catch (IOException var12) {
         var12.printStackTrace();
         return recipe;
      }
   }

}
