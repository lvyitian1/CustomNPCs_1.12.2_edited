package noppes.npcs;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

final class NoppesUtilServer$1 extends RConConsoleSource {
   // $FF: synthetic field
   final String val$name;
   // $FF: synthetic field
   final TextComponentString val$output;
   // $FF: synthetic field
   final BlockPos val$pos;
   // $FF: synthetic field
   final World val$world;
   // $FF: synthetic field
   final ICommandSender val$executer;

   NoppesUtilServer$1(MinecraftServer x0, String var2, TextComponentString var3, BlockPos var4, World var5, ICommandSender var6) {
      super(x0);
      this.val$name = var2;
      this.val$output = var3;
      this.val$pos = var4;
      this.val$world = var5;
      this.val$executer = var6;
   }

   public String getName() {
      return "@CustomNPCs-" + this.val$name;
   }

   public ITextComponent getDisplayName() {
      return new TextComponentString(this.getName());
   }

   public void sendMessage(ITextComponent component) {
      this.val$output.appendSibling(component);
   }

   public boolean canUseCommand(int permLevel, String commandName) {
      if (CustomNpcs.NpcUseOpCommands) {
         return true;
      } else {
         return permLevel <= 2;
      }
   }

   public BlockPos getPosition() {
      return this.val$pos;
   }

   public Vec3d getPositionVector() {
      return new Vec3d((double)this.val$pos.getX() + 0.5D, (double)this.val$pos.getY() + 0.5D, (double)this.val$pos.getZ() + 0.5D);
   }

   public World getEntityWorld() {
      return this.val$world;
   }

   public Entity getCommandSenderEntity() {
      return this.val$executer == null ? null : this.val$executer.getCommandSenderEntity();
   }

   public boolean sendCommandFeedback() {
      return this.getServer().worlds[0].getGameRules().getBoolean("commandBlockOutput");
   }
}
