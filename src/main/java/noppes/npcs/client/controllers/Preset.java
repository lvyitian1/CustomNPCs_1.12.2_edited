package noppes.npcs.client.controllers;

import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartData;
import noppes.npcs.constants.EnumParts;

public class Preset {
   public ModelData data = new ModelData();
   public String name;

   public NBTTagCompound writeToNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("PresetName", this.name);
      compound.setTag("PresetData", this.data.writeToNBT());
      return compound;
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.name = compound.getString("PresetName");
      this.data.readFromNBT(compound.getCompoundTag("PresetData"));
   }

   public static void FillDefault(HashMap<String, Preset> presets) {
      ModelData data = new ModelData();
      Preset preset = new Preset();
      preset.name = "Elf Male";
      preset.data = data;
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.85F, 1.15F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.85F, 1.15F);
      data.getPartConfig(EnumParts.BODY).setScale(0.85F, 1.15F);
      data.getPartConfig(EnumParts.HEAD).setScale(0.85F, 0.95F);
      presets.put("elf male", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Elf Female";
      preset.data = data;
      data.getOrCreatePart(EnumParts.BREASTS).type = 2;
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.8F, 1.05F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8F, 1.05F);
      data.getPartConfig(EnumParts.BODY).setScale(0.8F, 1.05F);
      data.getPartConfig(EnumParts.HEAD).setScale(0.8F, 0.85F);
      presets.put("elf female", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Dwarf Male";
      preset.data = data;
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.1F, 0.7F, 0.9F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.9F, 0.7F);
      data.getPartConfig(EnumParts.BODY).setScale(1.2F, 0.7F, 1.5F);
      data.getPartConfig(EnumParts.HEAD).setScale(0.85F, 0.85F);
      presets.put("dwarf male", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Dwarf Female";
      preset.data = data;
      data.getOrCreatePart(EnumParts.BREASTS).type = 2;
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.9F, 0.65F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.9F, 0.65F);
      data.getPartConfig(EnumParts.BODY).setScale(1.0F, 0.65F, 1.1F);
      data.getPartConfig(EnumParts.HEAD).setScale(0.85F, 0.85F);
      presets.put("dwarf female", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Orc Male";
      preset.data = data;
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.2F, 1.05F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(1.2F, 1.05F);
      data.getPartConfig(EnumParts.BODY).setScale(1.4F, 1.1F, 1.5F);
      data.getPartConfig(EnumParts.HEAD).setScale(1.2F, 1.1F);
      presets.put("orc male", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Orc Female";
      preset.data = data;
      data.getOrCreatePart(EnumParts.BREASTS).type = 2;
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(1.1F, 1.0F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(1.1F, 1.0F);
      data.getPartConfig(EnumParts.BODY).setScale(1.1F, 1.0F, 1.25F);
      presets.put("orc female", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Human Male";
      preset.data = data;
      presets.put("human male", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Human Female";
      preset.data = data;
      data.getOrCreatePart(EnumParts.BREASTS).type = 2;
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.92F, 0.92F);
      data.getPartConfig(EnumParts.HEAD).setScale(0.95F, 0.95F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8F, 0.92F);
      data.getPartConfig(EnumParts.BODY).setScale(0.92F, 0.92F);
      presets.put("human female", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Cat Male";
      preset.data = data;
      ModelPartData ears = data.getOrCreatePart(EnumParts.EARS);
      ears.type = 0;
      ears.color = 14263886;
      ModelPartData snout = data.getOrCreatePart(EnumParts.SNOUT);
      snout.type = 0;
      snout.color = 14263886;
      ModelPartData tail = data.getOrCreatePart(EnumParts.TAIL);
      tail.type = 0;
      tail.color = 14263886;
      presets.put("cat male", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Cat Female";
      preset.data = data;
      ears = data.getOrCreatePart(EnumParts.EARS);
      ears.type = 0;
      ears.color = 14263886;
      snout = data.getOrCreatePart(EnumParts.SNOUT);
      snout.type = 0;
      snout.color = 14263886;
      tail = data.getOrCreatePart(EnumParts.TAIL);
      tail.type = 0;
      tail.color = 14263886;
      data.getOrCreatePart(EnumParts.BREASTS).type = 2;
      data.getPartConfig(EnumParts.HEAD).setScale(0.95F, 0.95F);
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.92F, 0.92F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8F, 0.92F);
      data.getPartConfig(EnumParts.BODY).setScale(0.92F, 0.92F);
      presets.put("cat female", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Wolf Male";
      preset.data = data;
      ears = data.getOrCreatePart(EnumParts.EARS);
      ears.type = 0;
      ears.color = 6182997;
      snout = data.getOrCreatePart(EnumParts.SNOUT);
      snout.type = 2;
      snout.color = 6182997;
      tail = data.getOrCreatePart(EnumParts.TAIL);
      tail.type = 0;
      tail.color = 6182997;
      presets.put("wolf male", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Wolf Female";
      preset.data = data;
      ears = data.getOrCreatePart(EnumParts.EARS);
      ears.type = 0;
      ears.color = 6182997;
      snout = data.getOrCreatePart(EnumParts.SNOUT);
      snout.type = 2;
      snout.color = 6182997;
      tail = data.getOrCreatePart(EnumParts.TAIL);
      tail.type = 0;
      tail.color = 6182997;
      data.getOrCreatePart(EnumParts.BREASTS).type = 2;
      data.getPartConfig(EnumParts.HEAD).setScale(0.95F, 0.95F);
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.92F, 0.92F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8F, 0.92F);
      data.getPartConfig(EnumParts.BODY).setScale(0.92F, 0.92F);
      presets.put("wolf female", preset);
      data = new ModelData();
      preset = new Preset();
      preset.name = "Enderchibi";
      preset.data = data;
      data.getPartConfig(EnumParts.LEG_LEFT).setScale(0.65F, 0.75F);
      data.getPartConfig(EnumParts.ARM_LEFT).setScale(0.5F, 1.45F);
      ModelPartData part = data.getOrCreatePart(EnumParts.PARTICLES);
      part.type = 1;
      part.color = 16711680;
      presets.put("enderchibi", preset);
   }
}
