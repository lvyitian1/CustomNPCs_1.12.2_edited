package noppes.npcs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import noppes.npcs.client.AnalyticsTracking;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.entity.data.DataScenes$SceneContainer;
import noppes.npcs.entity.data.DataScenes$SceneState;

public class ServerTickHandler {
   public int ticks = 0;
   private String serverName = null;

   @SubscribeEvent
   public void onServerTick(WorldTickEvent event) {
      if (event.phase == Phase.START) {
         NPCSpawning.findChunksForSpawning((WorldServer)event.world);
      }

   }

   @SubscribeEvent
   public void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.START && this.ticks++ >= 20) {
         SchematicController.Instance.updateBuilding();
         MassBlockController.Update();
         this.ticks = 0;

         for(DataScenes$SceneState state : DataScenes.StartedScenes.values()) {
            if (!state.paused) {
               ++state.ticks;
            }
         }

         for(DataScenes$SceneContainer entry : DataScenes.ScenesToRun) {
            entry.update();
         }

         DataScenes.ScenesToRun = new ArrayList();
      }

   }

   @SubscribeEvent
   public void playerLogin(PlayerLoggedInEvent event) {
      if (this.serverName == null) {
         String e = "local";
         MinecraftServer server = event.player.getServer();
         if (server.isDedicatedServer()) {
            try {
               e = InetAddress.getByName(server.getServerHostname()).getCanonicalHostName();
            } catch (UnknownHostException var5) {
               e = server.getServerHostname();
            }

            if (server.getServerPort() != 25565) {
               e = e + ":" + server.getServerPort();
            }
         }

         if (e == null || e.startsWith("192.168") || e.contains("127.0.0.1") || e.startsWith("localhost")) {
            e = "local";
         }

         this.serverName = e;
      }

      AnalyticsTracking.sendData(event.player, "join", this.serverName);
      SyncController.syncPlayer((EntityPlayerMP)event.player);
   }
}
