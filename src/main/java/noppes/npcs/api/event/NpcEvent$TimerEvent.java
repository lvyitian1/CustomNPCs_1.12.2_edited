package noppes.npcs.api.event;

import noppes.npcs.api.entity.ICustomNpc;

public class NpcEvent$TimerEvent extends NpcEvent {
   public final int id;

   public NpcEvent$TimerEvent(ICustomNpc npc, int id) {
      super(npc);
      this.id = id;
   }
}
