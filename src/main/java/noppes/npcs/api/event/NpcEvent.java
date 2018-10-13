package noppes.npcs.api.event;

import noppes.npcs.api.entity.ICustomNpc;

public class NpcEvent extends CustomNPCsEvent {
   public final ICustomNpc npc;

   public NpcEvent(ICustomNpc npc) {
      this.npc = npc;
   }
}
