package noppes.npcs.command;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import noppes.npcs.api.CommandNoppesBase;
import noppes.npcs.api.CommandNoppesBase$SubCommand;
import noppes.npcs.category.CategoryManager;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.controllers.LinkedNpcController$LinkedData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;
import org.apache.commons.lang3.ArrayUtils;
import scala.reflect.internal.Trees;

public class CmdNPC extends CommandNoppesBase {
   public EntityNPCInterface selectedNpc;

   public String getName() {
      return "npc";
   }

   public String getDescription() {
      return "NPC operation";
   }

   public String getUsage() {
      return "<name> <command>";
   }

   public boolean runSubCommands() {
      return false;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      String npcname = args[0].replace("%", " ");
      String command = args[1];
      args = (String[])Arrays.copyOfRange(args, 2, args.length);
      if (command.equalsIgnoreCase("create")) {
         args = (String[])ArrayUtils.add(args, 0, npcname);
         this.executeSub(server, sender, command, args);
      } else {
         for(EntityNPCInterface npc : this.getEntities(EntityNPCInterface.class, sender.getEntityWorld(), sender.getPosition(), 80)) {
            String name = npc.display.getName().replace(" ", "_");
            if (name.equalsIgnoreCase(npcname) && (this.selectedNpc == null || this.selectedNpc.getDistanceSq(sender.getPosition()) > npc.getDistanceSq(sender.getPosition()))) {
               this.selectedNpc = npc;
            }
         }

         if (this.selectedNpc == null) {
            throw new CommandException("Npc '%s' was not found", new Object[]{npcname});
         } else {
            this.executeSub(server, sender, command, args);
            this.selectedNpc = null;
         }
      }
   }
   @CommandNoppesBase$SubCommand(desc="Set ShowInCreativeInventory(default 3)",usage="<0/1/2/3>",permission = 2)
   public void creativeInventory(MinecraftServer server,ICommandSender sender, String[] args){
      if(args.length<1)
      {
         sender.sendMessage(Objects.requireNonNull(ITextComponent.Serializer.fromJsonLenient("{\"text\":\"缺少参数!\"}")));
         return;
      }
      int ci;
      try {
         ci=Integer.valueOf(args[0]);
      }catch(Throwable t){sender.sendMessage(Objects.requireNonNull(ITextComponent.Serializer.fromJsonLenient("{\"text\":\"参数1必须是有效的32位整数!\"}")));return;}
      if(ci<0 || ci>3){
         sender.sendMessage(Objects.requireNonNull(ITextComponent.Serializer.fromJsonLenient("{\"text\":\"参数1的值必须在0到3之间!\"}")));
         return;
      }
      if(this.selectedNpc.linkedData==null) {LinkedNpcController.Instance.addData(this.selectedNpc.linkedName); this.selectedNpc.linkedData=LinkedNpcController.Instance.getData(this.selectedNpc.linkedName);}
      CategoryManager.INSTANCE.setCreativeInventory(this.selectedNpc.linkedData,LinkedNpcController$LinkedData.ShowInCreativeInventory.values()[ci]);
      sender.sendMessage(Objects.requireNonNull(ITextComponent.Serializer.fromJsonLenient("{\"text\":\"已设置!\"}")));
   }
   @CommandNoppesBase$SubCommand(
      desc = "Set Home (respawn place)",
      usage = "[x] [y] [z]",
      permission = 2
   )
   public void home(MinecraftServer server, ICommandSender sender, String[] args) {
      BlockPos pos = sender.getPosition();
      if (args.length == 3) {
         try {
            pos = CommandBase.parseBlockPos(sender, args, 0, false);
         } catch (NumberInvalidException var6) {
            ;
         }
      }

      this.selectedNpc.ais.setStartPos(pos);
   }

   @CommandNoppesBase$SubCommand(
      desc = "Set NPC visibility",
      usage = "[true/false/semi]",
      permission = 2
   )
   public void visible(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length >= 1) {
         boolean bo = args[0].equalsIgnoreCase("true");
         boolean semi = args[0].equalsIgnoreCase("semi");
         int current = this.selectedNpc.display.getVisible();
         if (semi) {
            this.selectedNpc.display.setVisible(2);
         } else if (bo) {
            this.selectedNpc.display.setVisible(0);
         } else {
            this.selectedNpc.display.setVisible(1);
         }

      }
   }

   @CommandNoppesBase$SubCommand(
      desc = "Delete an NPC"
   )
   public void delete(MinecraftServer server, ICommandSender sender, String[] args) {
      this.selectedNpc.delete();
   }

   @CommandNoppesBase$SubCommand(
      desc = "Sets the owner of an follower/companion",
      usage = "[player]",
      permission = 2
   )
   public void owner(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length < 1) {
         EntityPlayer player = null;
         if (this.selectedNpc.roleInterface instanceof RoleFollower) {
            player = ((RoleFollower)this.selectedNpc.roleInterface).owner;
         }

         if (this.selectedNpc.roleInterface instanceof RoleCompanion) {
            player = ((RoleCompanion)this.selectedNpc.roleInterface).owner;
         }

         if (player == null) {
            this.sendMessage(sender, "No owner", new Object[0]);
         } else {
            this.sendMessage(sender, "Owner is: " + player.getName(), new Object[0]);
         }
      } else {
         EntityPlayerMP player = null;

         try {
            player = CommandBase.getPlayer(server, sender, args[0]);
         } catch (PlayerNotFoundException var6) {
            ;
         } catch (CommandException var7) {
            ;
         }

         if (this.selectedNpc.roleInterface instanceof RoleFollower) {
            ((RoleFollower)this.selectedNpc.roleInterface).setOwner(player);
         }

         if (this.selectedNpc.roleInterface instanceof RoleCompanion) {
            ((RoleCompanion)this.selectedNpc.roleInterface).setOwner(player);
         }
      }

   }

   @CommandNoppesBase$SubCommand(
      desc = "Set NPC name",
      usage = "[name]",
      permission = 2
   )
   public void name(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length >= 1) {
         String name = args[0];

         for(int i = 1; i < args.length; ++i) {
            name = name + " " + args[i];
         }

         if (!this.selectedNpc.display.getName().equals(name)) {
            this.selectedNpc.display.setName(name);
            this.selectedNpc.updateClient = true;
         }

      }
   }

   @CommandNoppesBase$SubCommand(
      desc = "Resets the npc",
      usage = "[name]",
      permission = 2
   )
   public void reset(MinecraftServer server, ICommandSender sender, String[] args) {
      this.selectedNpc.reset();
   }

   @CommandNoppesBase$SubCommand(
      desc = "Creates an NPC",
      usage = "[name]"
   )
   public void create(MinecraftServer server, ICommandSender sender, String[] args) {
      World pw = sender.getEntityWorld();
      EntityCustomNpc npc = new EntityCustomNpc(pw);
      if (args.length > 0) {
         npc.display.setName(args[0]);
      }

      BlockPos pos = sender.getPosition();
      npc.setPositionAndRotation((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 0.0F, 0.0F);
      npc.ais.setStartPos(pos);
      pw.spawnEntity(npc);
      npc.setHealth(npc.getMaxHealth());
   }

   public List getTabCompletions(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
      if (args.length == 2) {
         return CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"create", "home", "visible", "delete", "owner", "name","creativeInventory"});
      } else {
         return args.length == 3 && args[1].equalsIgnoreCase("owner") ? CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : (args.length==3&&args[1].equalsIgnoreCase("creativeInventory")?Lists.newArrayList("0","1","2","3"):null);
      }
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public <T extends Entity> List<T> getEntities(Class<? extends T> cls, World world, BlockPos pos, int range) {
      return world.getEntitiesWithinAABB(cls, (new AxisAlignedBB(pos, pos.add(1, 1, 1))).grow((double)range, (double)range, (double)range));
   }
}
