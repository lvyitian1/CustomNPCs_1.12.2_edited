package noppes.npcs.client.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityFakeLiving;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiCreationExtra extends GuiCreationScreenInterface implements ICustomScrollListener {
   private final String[] ignoredTags = new String[]{"CanBreakDoors", "Bred", "PlayerCreated", "HasReproduced"};
   private final String[] booleanTags = new String[0];
   private GuiCustomScroll scroll;
   private Map<String, GuiCreationExtra$GuiType> data = new HashMap();
   private GuiCreationExtra$GuiType selected;

   public GuiCreationExtra(EntityNPCInterface npc) {
      super(npc);
      this.active = 2;
   }

   public void initGui() {
      super.initGui();
      if (this.entity == null) {
         this.openGui(new GuiCreationParts(this.npc));
      } else {
         if (this.scroll == null) {
            this.data = this.getData(this.entity);
            this.scroll = new GuiCustomScroll(this, 0);
            List<String> list = new ArrayList(this.data.keySet());
            this.scroll.setList(list);
            if (list.isEmpty()) {
               return;
            }

            this.scroll.setSelected((String)list.get(0));
         }

         this.selected = (GuiCreationExtra$GuiType)this.data.get(this.scroll.getSelected());
         if (this.selected != null) {
            this.scroll.guiLeft = this.guiLeft;
            this.scroll.guiTop = this.guiTop + 46;
            this.scroll.setSize(100, this.ySize - 74);
            this.addScroll(this.scroll);
            this.selected.initGui();
         }
      }
   }

   public Map<String, GuiCreationExtra$GuiType> getData(EntityLivingBase entity) {
      Map<String, GuiCreationExtra$GuiType> data = new HashMap();
      NBTTagCompound compound = this.getExtras(entity);

      for(String name : compound.getKeySet()) {
         if (!this.isIgnored(name)) {
            NBTBase base = compound.getTag(name);
            if (name.equals("Age")) {
               data.put("Child", new GuiCreationExtra$GuiTypeBoolean(this, "Child", entity.isChild()));
            } else if (name.equals("Color") && base.getId() == 1) {
               data.put("Color", new GuiCreationExtra$GuiTypeByte(this, "Color", compound.getByte("Color")));
            } else if (base.getId() == 1) {
               byte b = ((NBTTagByte)base).getByte();
               if (b == 0 || b == 1) {
                  if (this.playerdata.extra.hasKey(name)) {
                     b = this.playerdata.extra.getByte(name);
                  }

                  data.put(name, new GuiCreationExtra$GuiTypeBoolean(this, name, b == 1));
               }
            }
         }
      }

      if (PixelmonHelper.isPixelmon(entity)) {
         data.put("Model", new GuiCreationExtra$GuiTypePixelmon(this, "Model"));
      }

      if (EntityList.getEntityString(entity).equals("tgvstyle.Dog")) {
         data.put("Breed", new GuiCreationExtra$GuiTypeDoggyStyle(this, "Breed"));
      }

      return data;
   }

   private boolean isIgnored(String tag) {
      for(String s : this.ignoredTags) {
         if (s.equals(tag)) {
            return true;
         }
      }

      return false;
   }

   private void updateTexture() {
      EntityLivingBase entity = this.playerdata.getEntity(this.npc);
      RenderLivingBase render = (RenderLivingBase)this.mc.getRenderManager().getEntityRenderObject(entity);
      this.npc.display.setSkinTexture(NPCRendererHelper.getTexture(render, entity));
   }

   private NBTTagCompound getExtras(EntityLivingBase entity) {
      NBTTagCompound fake = new NBTTagCompound();
      (new EntityFakeLiving(entity.world)).writeEntityToNBT(fake);
      NBTTagCompound compound = new NBTTagCompound();
      entity.writeEntityToNBT(compound);

      for(String name : fake.getKeySet()) {
         compound.removeTag(name);
      }

      return compound;
   }

   public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
      if (scroll.id == 0) {
         this.initGui();
      } else if (this.selected != null) {
         this.selected.scrollClicked(i, j, k, scroll);
      }

   }

   protected void actionPerformed(GuiButton btn) {
      super.actionPerformed(btn);
      if (this.selected != null) {
         this.selected.actionPerformed(btn);
      }

   }

   // $FF: synthetic method
   static void access$000(GuiCreationExtra x0) {
      x0.updateTexture();
   }
}
