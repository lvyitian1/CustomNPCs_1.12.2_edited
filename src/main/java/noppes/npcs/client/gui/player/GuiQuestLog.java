package noppes.npcs.client.gui.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabQuests;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.translation.I18n;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.GuiButtonNextPage;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiMenuSideButton;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.ITopButtonListener;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.util.NaturalOrderComparator;

public class GuiQuestLog extends GuiNPCInterface implements ITopButtonListener, ICustomScrollListener {
   private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/standardbg.png");
   public HashMap<String, List<Quest>> activeQuests = new HashMap();
   private HashMap<String, Quest> categoryQuests = new HashMap();
   public Quest selectedQuest = null;
   public String selectedCategory = "";
   private EntityPlayer player;
   private GuiCustomScroll scroll;
   private HashMap<Integer, GuiMenuSideButton> sideButtons = new HashMap();
   private boolean noQuests = false;
   private boolean questDetails = true;
   private Minecraft mc = Minecraft.getMinecraft();

   public GuiQuestLog(EntityPlayer player) {
      this.player = player;
      this.xSize = 280;
      this.ySize = 180;
      this.drawDefaultBackground = false;
   }

   public void initGui() {
      super.initGui();

      for(Quest quest : PlayerQuestController.getActiveQuests(this.player)) {
         String category = quest.category.title;
         if (!this.activeQuests.containsKey(category)) {
            this.activeQuests.put(category, new ArrayList());
         }

         List<Quest> list = (List)this.activeQuests.get(category);
         list.add(quest);
      }

      this.sideButtons.clear();
      this.guiTop += 10;
      TabRegistry.updateTabValues(this.guiLeft, this.guiTop, InventoryTabQuests.class);
      TabRegistry.addTabsToList(this.buttonList);
      this.noQuests = false;
      if (this.activeQuests.isEmpty()) {
         this.noQuests = true;
      } else {
         List<String> categories = new ArrayList();
         categories.addAll(this.activeQuests.keySet());
         Collections.sort(categories, new NaturalOrderComparator());
         int i = 0;

         for(String category : categories) {
            if (this.selectedCategory.isEmpty()) {
               this.selectedCategory = category;
            }

            this.sideButtons.put(Integer.valueOf(i), new GuiMenuSideButton(i, this.guiLeft - 69, this.guiTop + 2 + i * 21, 70, 22, category));
            ++i;
         }

         ((GuiMenuSideButton)this.sideButtons.get(Integer.valueOf(categories.indexOf(this.selectedCategory)))).active = true;
         if (this.scroll == null) {
            this.scroll = new GuiCustomScroll(this, 0);
         }

         HashMap<String, Quest> categoryQuests = new HashMap();

         for(Quest q : this.activeQuests.get(this.selectedCategory)) {
            categoryQuests.put(q.title, q);
         }

         this.categoryQuests = categoryQuests;
         this.scroll.setList(new ArrayList(categoryQuests.keySet()));
         this.scroll.setSize(134, 174);
         this.scroll.guiLeft = this.guiLeft + 5;
         this.scroll.guiTop = this.guiTop + 15;
         this.addScroll(this.scroll);
         this.addButton(new GuiButtonNextPage(1, this.guiLeft + 286, this.guiTop + 176, true));
         this.addButton(new GuiButtonNextPage(2, this.guiLeft + 144, this.guiTop + 176, false));
         this.getButton(1).visible = this.questDetails && this.selectedQuest != null;
         this.getButton(2).visible = !this.questDetails && this.selectedQuest != null;
      }
   }

   protected void actionPerformed(GuiButton guibutton) {
      if (guibutton instanceof GuiButtonNextPage) {
         if (guibutton.id == 1) {
            this.questDetails = false;
            this.initGui();
         }

         if (guibutton.id == 2) {
            this.questDetails = true;
            this.initGui();
         }

      }
   }

   public void drawScreen(int i, int j, float f) {
      if (this.scroll != null) {
         this.scroll.visible = !this.noQuests;
      }

      this.drawDefaultBackground();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.renderEngine.bindTexture(this.resource);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 252, 195);
      this.drawTexturedModalRect(this.guiLeft + 252, this.guiTop, 188, 0, 67, 195);
      super.drawScreen(i, j, f);
      if (this.noQuests) {
         this.mc.fontRenderer.drawString(I18n.translateToLocal("quest.noquests"), this.guiLeft + 84, this.guiTop + 80, CustomNpcResourceListener.DefaultTextColor);
      } else {
         for(GuiMenuSideButton button : (GuiMenuSideButton[])this.sideButtons.values().toArray(new GuiMenuSideButton[this.sideButtons.size()])) {
            button.drawButton(this.mc, i, j, f);
         }

         this.mc.fontRenderer.drawString(this.selectedCategory, this.guiLeft + 5, this.guiTop + 5, CustomNpcResourceListener.DefaultTextColor);
         if (this.selectedQuest != null) {
            if (this.questDetails) {
               this.drawProgress();
               String title = I18n.translateToLocal("gui.text");
               this.mc.fontRenderer.drawString(title, this.guiLeft + 284 - this.mc.fontRenderer.getStringWidth(title), this.guiTop + 179, CustomNpcResourceListener.DefaultTextColor);
            } else {
               this.drawQuestText();
               String title = I18n.translateToLocal("quest.objectives");
               this.mc.fontRenderer.drawString(title, this.guiLeft + 168, this.guiTop + 179, CustomNpcResourceListener.DefaultTextColor);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(this.guiLeft + 148), (float)this.guiTop, 0.0F);
            GlStateManager.scale(1.24F, 1.24F, 1.24F);
            this.fontRenderer.drawString(this.selectedQuest.title, (130 - this.fontRenderer.getStringWidth(this.selectedQuest.title)) / 2, 4, CustomNpcResourceListener.DefaultTextColor);
            GlStateManager.popMatrix();
            this.drawHorizontalLine(this.guiLeft + 142, this.guiLeft + 312, this.guiTop + 17, -16777216 + CustomNpcResourceListener.DefaultTextColor);
         }
      }
   }

   private void drawQuestText() {
      TextBlockClient block = new TextBlockClient(this.selectedQuest.getLogText(), 174, true, new Object[]{this.player});
      int yoffset = this.guiTop + 5;

      for(int i = 0; i < block.lines.size(); ++i) {
         String text = ((ITextComponent)block.lines.get(i)).getFormattedText();
         this.fontRenderer.drawString(text, this.guiLeft + 142, this.guiTop + 20 + i * this.fontRenderer.FONT_HEIGHT, CustomNpcResourceListener.DefaultTextColor);
      }

   }

   private void drawProgress() {
      String complete = this.selectedQuest.getNpcName();
      if (complete != null && !complete.isEmpty()) {
         this.mc.fontRenderer.drawString(I18n.translateToLocalFormatted("quest.completewith", new Object[]{complete}), this.guiLeft + 144, this.guiTop + 105, CustomNpcResourceListener.DefaultTextColor);
      }

      int yoffset = this.guiTop + 22;

      for(String process : this.selectedQuest.questInterface.getQuestLogStatus(this.player)) {
         int index = process.lastIndexOf(":");
         if (index > 0) {
            String name = process.substring(0, index);
            String trans = I18n.translateToLocal(name);
            if (!trans.equals(name)) {
               name = trans;
            }

            trans = I18n.translateToLocal("entity." + name + ".name");
            if (!trans.equals("entity." + name + ".name")) {
               name = trans;
            }

            process = name + process.substring(index);
         }

         this.mc.fontRenderer.drawString("- " + process, this.guiLeft + 144, yoffset, CustomNpcResourceListener.DefaultTextColor);
         yoffset += 10;
      }

   }

   public void mouseClicked(int i, int j, int k) {
      super.mouseClicked(i, j, k);
      if (k == 0) {
         if (this.scroll != null) {
            this.scroll.mouseClicked(i, j, k);
         }

         for(GuiMenuSideButton button : this.sideButtons.values()) {
            if (button.mousePressed(this.mc, i, j)) {
               this.sideButtonPressed(button);
            }
         }
      }

   }

   private void sideButtonPressed(GuiMenuSideButton button) {
      if (!button.active) {
         NoppesUtil.clickSound();
         this.selectedCategory = button.displayString;
         this.selectedQuest = null;
         this.initGui();
      }
   }

   public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
      if (scroll.hasSelected()) {
         this.selectedQuest = (Quest)this.categoryQuests.get(scroll.getSelected());
         this.initGui();
      }
   }

   public void keyTyped(char c, int i) {
      if (i == 1 || i == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void save() {
   }
}
