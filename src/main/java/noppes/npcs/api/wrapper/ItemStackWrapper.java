package noppes.npcs.api.wrapper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemScripted;

public class ItemStackWrapper implements IItemStack, ICapabilityProvider, ICapabilitySerializable {
   private Map<String, Object> tempData = new HashMap();
   @CapabilityInject(ItemStackWrapper.class)
   public static Capability<ItemStackWrapper> ITEMSCRIPTEDDATA_CAPABILITY = null;
   private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
   public ItemStack item;
   private NBTTagCompound storedData = new NBTTagCompound();
   private final IData tempdata = new ItemStackWrapper$1(this);
   private final IData storeddata = new ItemStackWrapper$2(this);
   private static final ResourceLocation key = new ResourceLocation("customnpcs", "itemscripteddata");

   protected ItemStackWrapper(ItemStack item) {
      this.item = item;
   }

   public IData getTempdata() {
      return this.tempdata;
   }

   public IData getStoreddata() {
      return this.storeddata;
   }

   public int getStackSize() {
      return this.item.getCount();
   }

   public void setStackSize(int size) {
      if (size > this.getMaxStackSize()) {
         throw new CustomNPCsException("Can't set the stacksize bigger than MaxStacksize", new Object[0]);
      } else {
         this.item.setCount(size);
      }
   }

   public void setAttribute(String name, double value) {
      NBTTagCompound compound = this.item.getTagCompound();
      if (compound == null) {
         this.item.setTagCompound(compound = new NBTTagCompound());
      }

      NBTTagList nbttaglist = compound.getTagList("AttributeModifiers", 10);
      NBTTagList newList = new NBTTagList();

      for(int i = 0; i < nbttaglist.tagCount(); ++i) {
         NBTTagCompound c = nbttaglist.getCompoundTagAt(i);
         if (!c.getString("AttributeName").equals(name)) {
            newList.appendTag(c);
         }
      }

      if (value > 0.0D) {
         NBTTagCompound nbttagcompound = SharedMonsterAttributes.writeAttributeModifierToNBT(new AttributeModifier(name, value, 0));
         nbttagcompound.setString("AttributeName", name);
         newList.appendTag(nbttagcompound);
      }

      compound.setTag("AttributeModifiers", newList);
   }

   public double getAttribute(String name) {
      NBTTagCompound compound = this.item.getTagCompound();
      if (compound == null) {
         return 0.0D;
      } else {
         Multimap<String, AttributeModifier> map = this.item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);

         for(Entry<String, AttributeModifier> entry : map.entries()) {
            if (((String)entry.getKey()).equals(name)) {
               AttributeModifier mod = (AttributeModifier)entry.getValue();
               return mod.getAmount();
            }
         }

         return 0.0D;
      }
   }

   public boolean hasAttribute(String name) {
      NBTTagCompound compound = this.item.getTagCompound();
      if (compound == null) {
         return false;
      } else {
         NBTTagList nbttaglist = compound.getTagList("AttributeModifiers", 10);

         for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound c = nbttaglist.getCompoundTagAt(i);
            if (c.getString("AttributeName").equals(name)) {
               return true;
            }
         }

         return false;
      }
   }

   public int getItemDamage() {
      return this.item.getItemDamage();
   }

   public void setItemDamage(int value) {
      this.item.setItemDamage(value);
   }

   public void addEnchantment(String id, int strenght) {
      Enchantment ench = Enchantment.getEnchantmentByLocation(id);
      if (ench == null) {
         throw new CustomNPCsException("Unknown enchant id:" + id, new Object[0]);
      } else {
         this.item.addEnchantment(ench, strenght);
      }
   }

   public boolean isEnchanted() {
      return this.item.isItemEnchanted();
   }

   public boolean hasEnchant(int id) {
      if (!this.isEnchanted()) {
         return false;
      } else {
         NBTTagList list = this.item.getEnchantmentTagList();

         for(int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            if (compound.getShort("id") == id) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isBlock() {
      Block block = Block.getBlockFromItem(this.item.getItem());
      return block != null && block != Blocks.AIR;
   }

   public boolean hasCustomName() {
      return this.item.hasDisplayName();
   }

   public void setCustomName(String name) {
      this.item.setStackDisplayName(name);
   }

   public String getDisplayName() {
      return this.item.getDisplayName();
   }

   public String getItemName() {
      return this.item.getItem().getItemStackDisplayName(this.item);
   }

   public String getName() {
      return Item.REGISTRY.getNameForObject(this.item.getItem()) + "";
   }

   public INbt getNbt() {
      NBTTagCompound compound = this.item.getTagCompound();
      if (compound == null) {
         this.item.setTagCompound(compound = new NBTTagCompound());
      }

      return NpcAPI.Instance().getINbt(compound);
   }

   public boolean hasNbt() {
      return this.item.hasTagCompound();
   }

   public ItemStack getMCItemStack() {
      return this.item;
   }

   public static ItemStack MCItem(IItemStack item) {
      return item == null ? ItemStack.EMPTY : item.getMCItemStack();
   }

   public void damageItem(int damage, IEntityLiving living) {
      this.item.damageItem(damage, living == null ? null : living.getMCEntity());
   }

   public boolean isBook() {
      return false;
   }

   public int getFoodLevel() {
      return this.item.getItem() instanceof ItemFood ? ((ItemFood)this.item.getItem()).getHealAmount(this.item) : 0;
   }

   public IItemStack copy() {
      return createNew(this.item.copy());
   }

   public int getMaxStackSize() {
      return this.item.getMaxStackSize();
   }

   public int getMaxItemDamage() {
      return this.item.getMaxDamage();
   }

   public INbt getItemNbt() {
      NBTTagCompound compound = new NBTTagCompound();
      this.item.writeToNBT(compound);
      return NpcAPI.Instance().getINbt(compound);
   }

   public double getAttackDamage() {
      HashMultimap map = (HashMultimap)this.item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
      Iterator iterator = map.entries().iterator();
      double damage = 0.0D;

      while(iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         if (entry.getKey().equals(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
            AttributeModifier mod = (AttributeModifier)entry.getValue();
            damage = mod.getAmount();
         }
      }

      damage = damage + (double)EnchantmentHelper.getModifierForCreature(this.item, EnumCreatureAttribute.UNDEFINED);
      return damage;
   }

   public boolean isEmpty() {
      return this.item.isEmpty();
   }

   public int getType() {
      if (this.item.getItem() instanceof IPlantable) {
         return 5;
      } else {
         return this.item.getItem() instanceof ItemSword ? 4 : 0;
      }
   }

   public boolean isWearable() {
      for(EntityEquipmentSlot slot : VALID_EQUIPMENT_SLOTS) {
         if (this.item.getItem().isValidArmor(this.item, slot, EntityNPCInterface.ChatEventPlayer)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == ITEMSCRIPTEDDATA_CAPABILITY;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(this.hasCapability(capability, facing) ? this : null);
   }

   public static void register(AttachCapabilitiesEvent<ItemStack> event) {
      ItemStackWrapper wrapper = createNew((ItemStack)event.getObject());
      event.addCapability(key, wrapper);
   }

   private static ItemStackWrapper createNew(ItemStack item) {
      if (item == null) {
         return new ItemStackWrapper(ItemStack.EMPTY);
      } else if (item.getItem() instanceof ItemScripted) {
         return new ItemScriptedWrapper(item);
      } else if (item.getItem() != Items.WRITTEN_BOOK && item.getItem() != Items.WRITABLE_BOOK && !(item.getItem() instanceof ItemWritableBook) && !(item.getItem() instanceof ItemWrittenBook)) {
         if (item.getItem() instanceof ItemArmor) {
            return new ItemArmorWrapper(item);
         } else {
            Block block = Block.getBlockFromItem(item.getItem());
            return (ItemStackWrapper)(block != null ? new ItemBlockWrapper(item) : new ItemStackWrapper(item));
         }
      } else {
         return new ItemBookWrapper(item);
      }
   }

   public String[] getLore() {
      NBTTagCompound compound = this.item.getSubCompound("display");
      if (compound != null && compound.getTagId("Lore") == 9) {
         NBTTagList nbttaglist = compound.getTagList("Lore", 8);
         if (nbttaglist.isEmpty()) {
            return new String[0];
         } else {
            List<String> lore = new ArrayList();

            for(int i = 0; i < nbttaglist.tagCount(); ++i) {
               lore.add(nbttaglist.getStringTagAt(i));
            }

            return (String[])lore.toArray(new String[lore.size()]);
         }
      } else {
         return new String[0];
      }
   }

   public void setLore(String[] lore) {
      NBTTagCompound compound = this.item.getOrCreateSubCompound("display");
      if (lore != null && lore.length != 0) {
         NBTTagList nbtlist = new NBTTagList();

         for(String s : lore) {
            nbtlist.appendTag(new NBTTagString(s));
         }

         compound.setTag("Lore", nbtlist);
      } else {
         compound.removeTag("Lore");
      }
   }

   public NBTBase serializeNBT() {
      return this.getNBT();
   }

   public void deserializeNBT(NBTBase nbt) {
      this.setNBT((NBTTagCompound)nbt);
   }

   public NBTTagCompound getNBT() {
      return new NBTTagCompound();
   }

   public void setNBT(NBTTagCompound compound) {
   }

   // $FF: synthetic method
   static Map access$000(ItemStackWrapper x0) {
      return x0.tempData;
   }

   // $FF: synthetic method
   static NBTTagCompound access$100(ItemStackWrapper x0) {
      return x0.storedData;
   }

   // $FF: synthetic method
   static NBTTagCompound access$102(ItemStackWrapper x0, NBTTagCompound x1) {
      return x0.storedData = x1;
   }
}
