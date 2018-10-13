package noppes.npcs.command;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.api.CommandNoppesBase$SubCommand;
import noppes.npcs.controllers.data.MarkData;

public class CmdMark extends CommandNoppesBase {
   public String getName() {
      return "mark";
   }

   public String getDescription() {
      return "Mark operations";
   }

   @CommandNoppesBase$SubCommand(
      desc = "Set mark (warning overrides existing marks)",
      usage = "<@e> <type> [color]"
   )
   public void set(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      List<Entity> list = getEntityList(server, sender, args[0]);
      int type = 0;

      try {
         type = Integer.parseInt(args[1]);
      } catch (Exception var11) {
         ;
      }

      int color = 16777215;
      if (args.length > 2) {
         try {
            color = Integer.parseInt(args[2], 16);
         } catch (Exception var10) {
            ;
         }
      }

      for(Entity e : list) {
         if (e instanceof EntityLivingBase) {
            MarkData data = MarkData.get((EntityLivingBase)e);
            data.marks.clear();
            data.addMark(type, color);
         }
      }

   }

   @CommandNoppesBase$SubCommand(
      desc = "Clear mark",
      usage = "<@e>"
   )
   public void clear(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      for(Entity e : getEntityList(server, sender, args[0])) {
         if (e instanceof EntityLivingBase) {
            MarkData data = MarkData.get((EntityLivingBase)e);
            data.marks.clear();
            data.syncClients();
         }
      }

   }
}
