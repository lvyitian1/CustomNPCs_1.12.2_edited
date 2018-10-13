package noppes.npcs.command;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.Server;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.api.CommandNoppesBase$SubCommand;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;

public class CmdQuest extends CommandNoppesBase {
   public String getName() {
      return "quest";
   }

   public String getDescription() {
      return "Quest operations";
   }

   @CommandNoppesBase$SubCommand(
      desc = "Start a quest",
      usage = "<player> <quest>",
      permission = 2
   )
   public void start(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      String playername = args[0];

      int questid;
      try {
         questid = Integer.parseInt(args[1]);
      } catch (NumberFormatException var11) {
         throw new CommandException("QuestID must be an integer", new Object[0]);
      }

      List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
      if (data.isEmpty()) {
         throw new CommandException("Unknow player '%s'", new Object[]{playername});
      } else {
         Quest quest = (Quest)QuestController.instance.quests.get(Integer.valueOf(questid));
         if (quest == null) {
            throw new CommandException("Unknown QuestID", new Object[0]);
         } else {
            for(PlayerData playerdata : data) {
               QuestData questdata = new QuestData(quest);
               playerdata.questData.activeQuests.put(Integer.valueOf(questid), questdata);
               playerdata.save(true);
               Server.sendData((EntityPlayerMP)playerdata.player, EnumPacketClient.MESSAGE, "quest.newquest", quest.title, Integer.valueOf(2));
               Server.sendData((EntityPlayerMP)playerdata.player, EnumPacketClient.CHAT, "quest.newquest", ": ", quest.title);
            }

         }
      }
   }

   @CommandNoppesBase$SubCommand(
      desc = "Finish a quest",
      usage = "<player> <quest>"
   )
   public void finish(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      String playername = args[0];

      int questid;
      try {
         questid = Integer.parseInt(args[1]);
      } catch (NumberFormatException var10) {
         throw new CommandException("QuestID must be an integer", new Object[0]);
      }

      List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
      if (data.isEmpty()) {
         throw new CommandException(String.format("Unknow player '%s'", playername), new Object[0]);
      } else {
         Quest quest = (Quest)QuestController.instance.quests.get(Integer.valueOf(questid));
         if (quest == null) {
            throw new CommandException("Unknown QuestID", new Object[0]);
         } else {
            for(PlayerData playerdata : data) {
               playerdata.questData.finishedQuests.put(Integer.valueOf(questid), Long.valueOf(System.currentTimeMillis()));
               playerdata.save(true);
            }

         }
      }
   }

   @CommandNoppesBase$SubCommand(
      desc = "Stop a started quest",
      usage = "<player> <quest>",
      permission = 2
   )
   public void stop(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      String playername = args[0];

      int questid;
      try {
         questid = Integer.parseInt(args[1]);
      } catch (NumberFormatException var10) {
         throw new CommandException("QuestID must be an integer", new Object[0]);
      }

      List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
      if (data.isEmpty()) {
         throw new CommandException(String.format("Unknow player '%s'", playername), new Object[0]);
      } else {
         Quest quest = (Quest)QuestController.instance.quests.get(Integer.valueOf(questid));
         if (quest == null) {
            throw new CommandException("Unknown QuestID", new Object[0]);
         } else {
            for(PlayerData playerdata : data) {
               playerdata.questData.activeQuests.remove(Integer.valueOf(questid));
               playerdata.save(true);
            }

         }
      }
   }

   @CommandNoppesBase$SubCommand(
      desc = "Removes a quest from finished and active quests",
      usage = "<player> <quest>",
      permission = 2
   )
   public void remove(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      String playername = args[0];

      int questid;
      try {
         questid = Integer.parseInt(args[1]);
      } catch (NumberFormatException var10) {
         throw new CommandException("QuestID must be an integer", new Object[0]);
      }

      List<PlayerData> data = PlayerDataController.instance.getPlayersData(sender, playername);
      if (data.isEmpty()) {
         throw new CommandException(String.format("Unknow player '%s'", playername), new Object[0]);
      } else {
         Quest quest = (Quest)QuestController.instance.quests.get(Integer.valueOf(questid));
         if (quest == null) {
            throw new CommandException("Unknown QuestID", new Object[0]);
         } else {
            for(PlayerData playerdata : data) {
               playerdata.questData.activeQuests.remove(Integer.valueOf(questid));
               playerdata.questData.finishedQuests.remove(Integer.valueOf(questid));
               playerdata.save(true);
            }

         }
      }
   }

   @CommandNoppesBase$SubCommand(
      desc = "reload quests from disk",
      permission = 4
   )
   public void reload(MinecraftServer server, ICommandSender sender, String[] args) {
      (new QuestController()).load();
      SyncController.syncAllQuests(server);
   }
}
