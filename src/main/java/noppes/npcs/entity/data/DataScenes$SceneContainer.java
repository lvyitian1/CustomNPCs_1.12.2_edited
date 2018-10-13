package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.util.ValueUtil;

public class DataScenes$SceneContainer {
   public int btn;
   public String name;
   public String lines;
   public boolean enabled;
   public int ticks;
   private DataScenes$SceneState state;
   private List<DataScenes$SceneEvent> events;
   // $FF: synthetic field
   final DataScenes this$0;

   public DataScenes$SceneContainer(DataScenes this$0) {
      this.this$0 = this$0;
      this.btn = 0;
      this.name = "";
      this.lines = "";
      this.enabled = false;
      this.ticks = -1;
      this.state = null;
      this.events = new ArrayList();
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setBoolean("Enabled", this.enabled);
      compound.setString("Name", this.name);
      compound.setString("Lines", this.lines);
      compound.setInteger("Button", this.btn);
      compound.setInteger("Ticks", this.ticks);
      return compound;
   }

   public boolean validState() {
      if (!this.enabled) {
         return false;
      } else {
         if (this.state != null) {
            if (DataScenes.StartedScenes.containsValue(this.state)) {
               return !this.state.paused;
            }

            this.state = null;
         }

         this.state = (DataScenes$SceneState)DataScenes.StartedScenes.get(this.name.toLowerCase());
         if (this.state == null) {
            this.state = (DataScenes$SceneState)DataScenes.StartedScenes.get(this.btn + "btn");
         }

         if (this.state != null) {
            return !this.state.paused;
         } else {
            return false;
         }
      }
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.enabled = compound.getBoolean("Enabled");
      this.name = compound.getString("Name");
      this.lines = compound.getString("Lines");
      this.btn = compound.getInteger("Button");
      this.ticks = compound.getInteger("Ticks");
      this.events = new ArrayList();

      for(String line : this.lines.split("\r\n|\r|\n")) {
         DataScenes$SceneEvent event = DataScenes$SceneEvent.parse(line);
         if (event != null) {
            this.events.add(event);
         }
      }

      Collections.sort(this.events);
   }

   public void update() {
      if (this.enabled && !this.events.isEmpty() && this.state != null) {
         for(DataScenes$SceneEvent event : this.events) {
            if (event.ticks > this.state.ticks) {
               break;
            }

            if (event.ticks == this.state.ticks) {
               try {
                  this.handle(event);
               } catch (Exception var4) {
                  ;
               }
            }
         }

         this.ticks = this.state.ticks;
      }
   }

   private void handle(DataScenes$SceneEvent event) throws Exception {
      if (event.type == DataScenes$SceneType.MOVE) {
         String[] param = event.param.split(" ");

         while(param.length > 1) {
            boolean move = false;
            if (param[0].startsWith("to")) {
               move = true;
            } else if (!param[0].startsWith("tp")) {
               break;
            }

            BlockPos pos = null;
            if (param[0].startsWith("@")) {
               EntityLivingBase entitylivingbase = (EntityLivingBase)CommandBase.getEntity(DataScenes.access$000(this.this$0).getServer(), DataScenes.access$000(this.this$0), param[0], EntityLivingBase.class);
               if (entitylivingbase != null) {
                  pos = entitylivingbase.getPosition();
               }

               param = (String[])Arrays.copyOfRange(param, 2, param.length);
            } else {
               if (param.length < 4) {
                  return;
               }

               pos = CommandBase.parseBlockPos(DataScenes.access$000(this.this$0), param, 1, false);
               param = (String[])Arrays.copyOfRange(param, 4, param.length);
            }

            if (pos != null) {
               DataScenes.access$000(this.this$0).ais.setStartPos(pos);
               DataScenes.access$000(this.this$0).getNavigator().clearPath();
               if (move) {
                  Path pathentity = DataScenes.access$000(this.this$0).getNavigator().getPathToPos(pos);
                  DataScenes.access$000(this.this$0).getNavigator().setPath(pathentity, 1.0D);
               } else if (!DataScenes.access$000(this.this$0).isInRange((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 2.0D)) {
                  DataScenes.access$000(this.this$0).setPosition((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
               }
            }
         }
      } else if (event.type == DataScenes$SceneType.SAY) {
         DataScenes.access$000(this.this$0).saySurrounding(new Line(event.param));
      } else if (event.type == DataScenes$SceneType.ROTATE) {
         DataScenes.access$000(this.this$0).lookAi.resetTask();
         if (event.param.startsWith("@")) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)CommandBase.getEntity(DataScenes.access$000(this.this$0).getServer(), DataScenes.access$000(this.this$0), event.param, EntityLivingBase.class);
            DataScenes.access$000(this.this$0).lookAi.rotate(DataScenes.access$000(this.this$0).world.getClosestPlayerToEntity(entitylivingbase, 30.0D));
         } else {
            DataScenes.access$000(this.this$0).lookAi.rotate(Integer.parseInt(event.param));
         }
      } else if (event.type == DataScenes$SceneType.EQUIP) {
         String[] args = event.param.split(" ");
         if (args.length < 2) {
            return;
         }

         IItemStack itemstack = null;
         if (!args[1].equalsIgnoreCase("none")) {
            Item item = CommandBase.getItemByText(DataScenes.access$000(this.this$0), args[1]);
            int i = args.length >= 3 ? CommandBase.parseInt(args[2], 1, 64) : 1;
            int j = args.length >= 4 ? CommandBase.parseInt(args[3]) : 0;
            itemstack = NpcAPI.Instance().getIItemStack(new ItemStack(item, i, j));
         }

         if (args[0].equalsIgnoreCase("main")) {
            DataScenes.access$000(this.this$0).inventory.weapons.put(Integer.valueOf(0), itemstack);
         } else if (args[0].equalsIgnoreCase("off")) {
            DataScenes.access$000(this.this$0).inventory.weapons.put(Integer.valueOf(2), itemstack);
         } else if (args[0].equalsIgnoreCase("proj")) {
            DataScenes.access$000(this.this$0).inventory.weapons.put(Integer.valueOf(1), itemstack);
         } else if (args[0].equalsIgnoreCase("head")) {
            DataScenes.access$000(this.this$0).inventory.armor.put(Integer.valueOf(0), itemstack);
         } else if (args[0].equalsIgnoreCase("body")) {
            DataScenes.access$000(this.this$0).inventory.armor.put(Integer.valueOf(1), itemstack);
         } else if (args[0].equalsIgnoreCase("legs")) {
            DataScenes.access$000(this.this$0).inventory.armor.put(Integer.valueOf(2), itemstack);
         } else if (args[0].equalsIgnoreCase("boots")) {
            DataScenes.access$000(this.this$0).inventory.armor.put(Integer.valueOf(3), itemstack);
         }
      } else if (event.type == DataScenes$SceneType.ATTACK) {
         if (event.param.equals("none")) {
            DataScenes.access$000(this.this$0).setAttackTarget((EntityLivingBase)null);
         } else {
            EntityLivingBase entity = (EntityLivingBase)CommandBase.getEntity(DataScenes.access$000(this.this$0).getServer(), DataScenes.access$000(this.this$0), event.param, EntityLivingBase.class);
            if (entity != null) {
               DataScenes.access$000(this.this$0).setAttackTarget(entity);
            }
         }
      } else if (event.type == DataScenes$SceneType.THROW) {
         String[] args = event.param.split(" ");
         EntityLivingBase entity = (EntityLivingBase)CommandBase.getEntity(DataScenes.access$000(this.this$0).getServer(), DataScenes.access$000(this.this$0), args[0], EntityLivingBase.class);
         if (entity == null) {
            return;
         }

         float damage = Float.parseFloat(args[1]);
         if (damage <= 0.0F) {
            damage = 0.01F;
         }

         ItemStack stack = ItemStackWrapper.MCItem(DataScenes.access$000(this.this$0).inventory.getProjectile());
         if (args.length > 2) {
            Item item = CommandBase.getItemByText(DataScenes.access$000(this.this$0), args[2]);
            stack = new ItemStack(item, 1, 0);
         }

         EntityProjectile projectile = DataScenes.access$000(this.this$0).shoot(entity, 100, stack, false);
         projectile.damage = damage;
      } else if (event.type == DataScenes$SceneType.ANIMATE) {
         DataScenes.access$000(this.this$0).animateAi.temp = 0;
         if (event.param.equalsIgnoreCase("sleep")) {
            DataScenes.access$000(this.this$0).animateAi.temp = 2;
         } else if (event.param.equalsIgnoreCase("sneak")) {
            DataScenes.access$000(this.this$0).ais.animationType = 4;
         } else if (event.param.equalsIgnoreCase("normal")) {
            DataScenes.access$000(this.this$0).ais.animationType = 0;
         } else if (event.param.equalsIgnoreCase("sit")) {
            DataScenes.access$000(this.this$0).animateAi.temp = 1;
         } else if (event.param.equalsIgnoreCase("crawl")) {
            DataScenes.access$000(this.this$0).ais.animationType = 7;
         } else if (event.param.equalsIgnoreCase("bow")) {
            DataScenes.access$000(this.this$0).animateAi.temp = 11;
         } else if (event.param.equalsIgnoreCase("yes")) {
            DataScenes.access$000(this.this$0).animateAi.temp = 13;
         } else if (event.param.equalsIgnoreCase("no")) {
            DataScenes.access$000(this.this$0).animateAi.temp = 12;
         }
      } else if (event.type == DataScenes$SceneType.COMMAND) {
         NoppesUtilServer.runCommand(DataScenes.access$000(this.this$0), DataScenes.access$000(this.this$0).getName(), event.param, (EntityPlayer)null);
      } else if (event.type == DataScenes$SceneType.STATS) {
         int i = event.param.indexOf(" ");
         if (i <= 0) {
            return;
         }

         String type = event.param.substring(0, i).toLowerCase();
         String value = event.param.substring(i).trim();

         try {
            if (type.equals("walking_speed")) {
               DataScenes.access$000(this.this$0).ais.setWalkingSpeed(ValueUtil.CorrectInt(Integer.parseInt(value), 0, 10));
            } else if (type.equals("size")) {
               DataScenes.access$000(this.this$0).display.setSize(ValueUtil.CorrectInt(Integer.parseInt(value), 1, 30));
            } else {
               NoppesUtilServer.NotifyOPs("Unknown scene stat: " + type);
            }
         } catch (NumberFormatException var7) {
            NoppesUtilServer.NotifyOPs("Unknown scene stat " + type + " value: " + value);
         }
      } else if (event.type == DataScenes$SceneType.FACTION) {
         DataScenes.access$000(this.this$0).setFaction(Integer.parseInt(event.param));
      } else if (event.type == DataScenes$SceneType.FOLLOW) {
         if (event.param.equalsIgnoreCase("none")) {
            DataScenes.access$102(this.this$0, (EntityLivingBase)null);
            DataScenes.access$202(this.this$0, (String)null);
         } else {
            EntityLivingBase entity = (EntityLivingBase)CommandBase.getEntity(DataScenes.access$000(this.this$0).getServer(), DataScenes.access$000(this.this$0), event.param, EntityLivingBase.class);
            if (entity == null) {
               return;
            }

            DataScenes.access$102(this.this$0, entity);
            DataScenes.access$202(this.this$0, this.name);
         }
      }

   }
}
