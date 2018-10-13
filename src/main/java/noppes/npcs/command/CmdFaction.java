package noppes.npcs.command;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.api.CommandNoppesBase$SubCommand;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerFactionData;

public class CmdFaction extends CommandNoppesBase {
   public Faction selectedFaction;
   public List<PlayerData> data;

   public String getName() {
      return "faction";
   }

   public String getDescription() {
      return "Faction operations";
   }

   public String getUsage() {
      return "<player> <faction> <command>";
   }

   public boolean runSubCommands() {
      return false;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      String playername = args[0];
      String factionname = args[1];
      this.data = PlayerDataController.instance.getPlayersData(sender, playername);
      if (this.data.isEmpty()) {
         throw new CommandException("Unknow player '%s'", new Object[]{playername});
      } else {
         try {
            this.selectedFaction = FactionController.instance.getFaction(Integer.parseInt(factionname));
         } catch (NumberFormatException var7) {
            this.selectedFaction = FactionController.instance.getFactionFromName(factionname);
         }

         if (this.selectedFaction == null) {
            throw new CommandException("Unknow facion '%s", new Object[]{factionname});
         } else {
            this.executeSub(server, sender, args[2], (String[])Arrays.copyOfRange(args, 3, args.length));
         }
      }
   }

   @CommandNoppesBase$SubCommand(
      desc = "Add points",
      usage = "<points>"
   )
   public void add(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      int points;
      try {
         points = Integer.parseInt(args[0]);
      } catch (NumberFormatException var9) {
         throw new CommandException("Must be an integer", new Object[0]);
      }

      int factionid = this.selectedFaction.id;

      for(PlayerData playerdata : this.data) {
         PlayerFactionData playerfactiondata = playerdata.factionData;
         playerfactiondata.increasePoints(playerdata.player, factionid, points);
         playerdata.save(true);
      }

   }

   @CommandNoppesBase$SubCommand(
      desc = "Substract points",
      usage = "<points>"
   )
   public void subtract(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      int points;
      try {
         points = Integer.parseInt(args[0]);
      } catch (NumberFormatException var9) {
         throw new CommandException("Must be an integer", new Object[0]);
      }

      int factionid = this.selectedFaction.id;

      for(PlayerData playerdata : this.data) {
         PlayerFactionData playerfactiondata = playerdata.factionData;
         playerfactiondata.increasePoints(playerdata.player, factionid, -points);
         playerdata.save(true);
      }

   }

   @CommandNoppesBase$SubCommand(
      desc = "Reset points to default"
   )
   public void reset(MinecraftServer server, ICommandSender sender, String[] args) {
      for(PlayerData playerdata : this.data) {
         playerdata.factionData.factionData.put(Integer.valueOf(this.selectedFaction.id), Integer.valueOf(this.selectedFaction.defaultPoints));
         playerdata.save(true);
      }

   }

   @CommandNoppesBase$SubCommand(
      desc = "Set points",
      usage = "<points>"
   )
   public void set(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      int points;
      try {
         points = Integer.parseInt(args[0]);
      } catch (NumberFormatException var8) {
         throw new CommandException("Must be an integer", new Object[0]);
      }

      for(PlayerData playerdata : this.data) {
         PlayerFactionData playerfactiondata = playerdata.factionData;
         playerfactiondata.factionData.put(Integer.valueOf(this.selectedFaction.id), Integer.valueOf(points));
         playerdata.save(true);
      }

   }

   @CommandNoppesBase$SubCommand(
      desc = "Drop relationship"
   )
   public void drop(MinecraftServer server, ICommandSender sender, String[] args) {
      for(PlayerData playerdata : this.data) {
         playerdata.factionData.factionData.remove(Integer.valueOf(this.selectedFaction.id));
         playerdata.save(true);
      }

   }

   public List getTabCompletions(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
      return args.length == 3 ? CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"add", "subtract", "set", "reset", "drop", "create"}) : null;
   }
}
