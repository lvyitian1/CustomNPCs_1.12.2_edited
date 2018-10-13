package noppes.npcs.controllers;

import java.util.List;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;

public interface MassBlockController$IMassBlock {
   EntityNPCInterface getNpc();

   int getRange();

   void processed(List<BlockData> var1);
}
