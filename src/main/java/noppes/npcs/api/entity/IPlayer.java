package noppes.npcs.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.data.IPixelmonPlayerData;
import noppes.npcs.api.item.IItemStack;

public interface IPlayer<T extends EntityPlayerMP> extends IEntityLivingBase<T> {
   String getDisplayName();

   boolean hasFinishedQuest(int var1);

   boolean hasActiveQuest(int var1);

   void startQuest(int var1);

   int factionStatus(int var1);

   void finishQuest(int var1);

   void stopQuest(int var1);

   void removeQuest(int var1);

   boolean hasReadDialog(int var1);

   void showDialog(int var1, String var2);

   void removeDialog(int var1);

   void addDialog(int var1);

   void addFactionPoints(int var1, int var2);

   int getFactionPoints(int var1);

   void message(String var1);

   int getGamemode();

   void setGamemode(int var1);

   int inventoryItemCount(IItemStack var1);

   int inventoryItemCount(String var1, int var2);

   IItemStack[] getInventory();

   boolean removeItem(IItemStack var1, int var2);

   boolean removeItem(String var1, int var2, int var3);

   void removeAllItems(IItemStack var1);

   boolean giveItem(IItemStack var1);

   boolean giveItem(String var1, int var2, int var3);

   void setSpawnpoint(int var1, int var2, int var3);

   void resetSpawnpoint();

   boolean hasAchievement(String var1);

   int getExpLevel();

   void setExpLevel(int var1);

   boolean hasPermission(String var1);

   IPixelmonPlayerData getPixelmonData();

   ITimers getTimers();

   void closeGui();

   T getMCEntity();

   IBlock getSpawnPoint();

   void setSpawnPoint(IBlock var1);

   int getHunger();

   void setHunger(int var1);

   void kick(String var1);

   void sendNotification(String var1, String var2, int var3);

   void clearData();


}
