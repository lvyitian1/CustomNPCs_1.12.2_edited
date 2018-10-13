package noppes.npcs.client;

import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.util.CustomNPCsScheduler;

public class Client {
   public static void sendData(EnumPacketServer enu, Object... obs) {
      CustomNPCsScheduler.runTack(new Client$1(enu, obs));
   }
}
