package noppes.npcs.client.gui.model;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.NPCRendererHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.entity.EntityNPC64x32;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiCreationEntities extends GuiCreationScreenInterface implements ICustomScrollListener {
   public HashMap<String, Class<? extends EntityLivingBase>> data = new HashMap();
   private List<String> list;
   private GuiCustomScroll scroll;
   private boolean resetToSelected = true;

   public GuiCreationEntities(EntityNPCInterface npc) {
      super(npc);

      for(EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
         String name = ent.getName();
         Class<? extends Entity> c = ent.getEntityClass();

         try {
            if (EntityLiving.class.isAssignableFrom(c) && c.getConstructor(World.class) != null && !Modifier.isAbstract(c.getModifiers()) && Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(c) instanceof RenderLivingBase) {
               String s = name.toString();
               if (!s.toLowerCase().contains("customnpc")) {
                  this.data.put(name.toString(), c.asSubclass(EntityLivingBase.class));
               }
            }
         } catch (SecurityException var7) {
            var7.printStackTrace();
         } catch (NoSuchMethodException var8) {
            ;
         }
      }

      this.data.put("NPC 64x32", EntityNPC64x32.class);
      this.list = new ArrayList(this.data.keySet());
      this.list.add("NPC");
      Collections.sort(this.list, String.CASE_INSENSITIVE_ORDER);
      this.active = 1;
      this.xOffset = 60;
   }

   public void initGui() {
      super.initGui();
      this.addButton(new GuiNpcButton(10, this.guiLeft, this.guiTop + 46, 120, 20, "Reset To NPC"));
      if (this.scroll == null) {
         this.scroll = new GuiCustomScroll(this, 0);
         this.scroll.setUnsortedList(this.list);
      }

      this.scroll.guiLeft = this.guiLeft;
      this.scroll.guiTop = this.guiTop + 68;
      this.scroll.setSize(100, this.ySize - 96);
      String selected = "NPC";
      if (this.entity != null) {
         for(Entry<String, Class<? extends EntityLivingBase>> en : this.data.entrySet()) {
            if (((Class)en.getValue()).toString().equals(this.entity.getClass().toString())) {
               selected = (String)en.getKey();
            }
         }
      }

      this.scroll.setSelected(selected);
      if (this.resetToSelected) {
         this.scroll.scrollTo(this.scroll.getSelected());
         this.resetToSelected = false;
      }

      this.addScroll(this.scroll);
   }

   protected void actionPerformed(GuiButton btn) {
      super.actionPerformed(btn);
      if (btn.id == 10) {
         this.playerdata.setEntityClass((Class)null);
         this.resetToSelected = true;
         this.initGui();
      }

   }

   public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
      this.playerdata.setEntityClass((Class)this.data.get(scroll.getSelected()));
      Entity entity = this.playerdata.getEntity(this.npc);
      if (entity != null) {
         RenderLivingBase render = (RenderLivingBase)this.mc.getRenderManager().getEntityClassRenderObject(entity.getClass());
         if (!NPCRendererHelper.getTexture(render, entity).equals(TextureMap.LOCATION_MISSING_TEXTURE.toString())) {
            this.npc.display.setSkinTexture(NPCRendererHelper.getTexture(render, entity));
         }
      } else {
         this.npc.display.setSkinTexture("customnpcs:textures/entity/humanmale/steve.png");
      }

      this.initGui();
   }
}
