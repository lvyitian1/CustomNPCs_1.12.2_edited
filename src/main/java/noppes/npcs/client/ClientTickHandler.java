package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.player.GuiQuestLog;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.constants.EnumPlayerPacket;

public class ClientTickHandler {
   private World prevWorld;
   private boolean otherContainer = false;

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onClientTick(ClientTickEvent event) {
      if (event.phase != Phase.END) {
         Minecraft mc = Minecraft.getMinecraft();
         if (mc.player != null && mc.player.openContainer instanceof ContainerPlayer) {
            if (this.otherContainer) {
               NoppesUtilPlayer.sendData(EnumPlayerPacket.CheckQuestCompletion);
               this.otherContainer = false;
            }
         } else {
            this.otherContainer = true;
         }

         ++CustomNpcs.ticks;
         ++RenderNPCInterface.LastTextureTick;
         if (this.prevWorld != mc.world) {
            this.prevWorld = mc.world;
            MusicController.Instance.stopMusic();
         }

      }
   }

   @SubscribeEvent
   public void onKey(KeyInputEvent event) {
      if (CustomNpcs.SceneButtonsEnabled) {
         if (ClientProxy.Scene1.isPressed()) {
            Client.sendData(EnumPacketServer.SceneStart, Integer.valueOf(1));
         }

         if (ClientProxy.Scene2.isPressed()) {
            Client.sendData(EnumPacketServer.SceneStart, Integer.valueOf(2));
         }

         if (ClientProxy.Scene3.isPressed()) {
            Client.sendData(EnumPacketServer.SceneStart, Integer.valueOf(3));
         }

         if (ClientProxy.SceneReset.isPressed()) {
            Client.sendData(EnumPacketServer.SceneReset);
         }
      }

      if (ClientProxy.QuestLog.isPressed()) {
         Minecraft mc = Minecraft.getMinecraft();
         if (mc.currentScreen == null) {
            NoppesUtil.openGUI(mc.player, new GuiQuestLog(mc.player));
         } else if (mc.currentScreen instanceof GuiQuestLog) {
            mc.setIngameFocus();
         }
      }

   }
}
