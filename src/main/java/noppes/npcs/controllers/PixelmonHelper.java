package noppes.npcs.controllers;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import noppes.npcs.LogWriter;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobSpawner;
import org.apache.logging.log4j.LogManager;

public class PixelmonHelper {
   public static boolean Enabled = false;
   private static Object PokeballManager = null;
   private static Method getPlayerStorage = null;
   private static Object ComputerManager = null;
   private static Method getPlayerComputerStorage = null;
   private static Constructor attackByID = null;
   private static Constructor attackByName = null;
   private static Field baseAttack = null;
   private static Field getAttackID = null;
   private static Field getAttackName = null;
   private static Method getPixelmonModel = null;

   public static void load() {
      Enabled = Loader.isModLoaded("pixelmon");
      if (Enabled) {
         try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity2HasModel");
            getPixelmonModel = c.getMethod("getModel");
         } catch (Exception var2) {
            ;
         }

         try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.storage.PixelmonStorage");
            PokeballManager = c.getField("pokeBallManager").get((Object)null);
            ComputerManager = c.getField("computerManager").get((Object)null);
            getPlayerStorage = PokeballManager.getClass().getMethod("getPlayerStorage", EntityPlayerMP.class);
            getPlayerComputerStorage = ComputerManager.getClass().getMethod("getPlayerStorage", EntityPlayerMP.class);
            c = Class.forName("com.pixelmonmod.pixelmon.battles.attacks.Attack");
            attackByID = c.getConstructor(Integer.TYPE);
            attackByName = c.getConstructor(String.class);
            baseAttack = c.getField("baseAttack");
            c = Class.forName("com.pixelmonmod.pixelmon.battles.attacks.AttackBase");
            getAttackID = c.getField("attackIndex");
            getAttackName = c.getDeclaredField("attackName");
            getAttackName.setAccessible(true);
         } catch (Exception var1) {
            LogWriter.except(var1);
         }

      }
   }

   public static List<String> getPixelmonList() {
      List<String> list = new ArrayList();
      if (!Enabled) {
         return list;
      } else {
         try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.enums.EnumPokemon");
            Object[] array = c.getEnumConstants();

            for(Object ob : array) {
               list.add(ob.toString());
            }
         } catch (Exception var7) {
            LogManager.getLogger().error("getPixelmonList", var7);
         }

         return list;
      }
   }

   public static boolean isPixelmon(Entity entity) {
      if (!Enabled) {
         return false;
      } else {
         String s = EntityList.getEntityString(entity);
         return s == null ? false : s.contains("Pixelmon");
      }
   }

   public static String getName(EntityLivingBase entity) {
      if (Enabled && isPixelmon(entity)) {
         try {
            Method m = entity.getClass().getMethod("getName");
            return m.invoke(entity).toString();
         } catch (Exception var2) {
            LogManager.getLogger().error("getName", var2);
            return "";
         }
      } else {
         return "";
      }
   }

   public static Object getModel(EntityLivingBase entity) {
      try {
         return getPixelmonModel.invoke(entity);
      } catch (Exception var2) {
         LogManager.getLogger().error("getModel", var2);
         return null;
      }
   }

   public static void debug(EntityLivingBase entity) {
      if (Enabled && isPixelmon(entity)) {
         try {
            Method m = entity.getClass().getMethod("getModel");
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString((String)m.invoke(entity)));
         } catch (Exception var2) {
            var2.printStackTrace();
         }

      }
   }

   public static boolean isTrainer(EntityLivingBase entity) {
      if (!Enabled) {
         return false;
      } else {
         String s = EntityList.getEntityString(entity);
         return s == null ? false : s.equals("pixelmon.Trainer");
      }
   }

   public static boolean isBattling(EntityPlayerMP player) {
      if (!Enabled) {
         return false;
      } else {
         try {
            Class c = Class.forName("com.pixelmonmod.pixelmon.battles.BattleRegistry");
            Method m = c.getMethod("getBattle", EntityPlayer.class);
            return m.invoke((Object)null, player) == null;
         } catch (Exception var3) {
            LogManager.getLogger().error("canBattle", var3);
            return false;
         }
      }
   }

   public static boolean isBattling(EntityLivingBase trainer) {
      if (Enabled && isTrainer(trainer)) {
         try {
            Field f = trainer.getClass().getField("battleController");
            return f.get(trainer) != null;
         } catch (Exception var2) {
            LogManager.getLogger().error("canBattle", var2);
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean canBattle(EntityPlayerMP player, EntityNPCInterface npc) {
      if (Enabled && npc.advanced.job == 6 && !isBattling(player)) {
         try {
            JobSpawner spawner = (JobSpawner)npc.jobInterface;
            if (spawner.isOnCooldown(player.getName())) {
               return false;
            } else {
               Optional<Object> op = (Optional)getPlayerStorage.invoke(PokeballManager, player);
               if (!op.isPresent()) {
                  return false;
               } else {
                  Object ob = op.get();
                  Method m = ob.getClass().getMethod("countAblePokemon");
                  return ((Integer)m.invoke(ob)).intValue() != 0;
               }
            }
         } catch (Exception var6) {
            LogManager.getLogger().error("canBattle", var6);
            return false;
         }
      } else {
         return false;
      }
   }

   public static EntityTameable pixelmonFromNBT(NBTTagCompound compound, EntityPlayer player) {
      if (!Enabled) {
         return null;
      } else {
         try {
            Optional<Object> op = (Optional)getPlayerStorage.invoke(PokeballManager, player);
            if (!op.isPresent()) {
               return null;
            } else {
               Object ob = op.get();
               Method sendOut = ob.getClass().getDeclaredMethod("sendOut", NBTTagCompound.class, World.class);
               sendOut.setAccessible(true);
               return (EntityTameable)sendOut.invoke(ob, compound, player.world);
            }
         } catch (Exception var5) {
            var5.printStackTrace();
            return null;
         }
      }
   }

   public static NBTTagCompound getPartySlot(int i, EntityPlayer player) {
      if (!Enabled) {
         return null;
      } else {
         try {
            Optional<Object> op = (Optional)getPlayerStorage.invoke(PokeballManager, player);
            if (!op.isPresent()) {
               return null;
            } else {
               Object ob = op.get();
               NBTTagCompound[] party = (NBTTagCompound[])ob.getClass().getFields()[0].get(ob);
               return party[i];
            }
         } catch (Exception var5) {
            var5.printStackTrace();
            return null;
         }
      }
   }

   public static boolean startBattle(EntityPlayerMP player, EntityLivingBase trainer) {
      if (!Enabled) {
         return false;
      } else {
         try {
            Optional<Object> op = (Optional)getPlayerStorage.invoke(PokeballManager, player);
            if (!op.isPresent()) {
               return false;
            } else {
               Object ob = op.get();
               Class c = ob.getClass();
               Method m = c.getMethod("getFirstAblePokemon", World.class);
               Entity pixelmon = (Entity)m.invoke(ob, player.world);
               Class cEntity = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon");
               m = c.getMethod("EntityAlreadyExists", cEntity);
               if (!((Boolean)m.invoke(ob, pixelmon)).booleanValue()) {
                  m = cEntity.getMethod("releaseFromPokeball");
                  pixelmon.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 0.0F);
               }

               c = Class.forName("com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant");
               Object parTrainer = c.getConstructor(trainer.getClass(), EntityPlayer.class, Integer.TYPE).newInstance(trainer, player, Integer.valueOf(1));
               //TODO: rlcai
               Object[] pixelmonArray = (Object[]) Array.newInstance(cEntity, 1);
               pixelmonArray[0] = pixelmon;
               c = Class.forName("com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant");
               Object parPlayer = c.getConstructor(EntityPlayerMP.class, pixelmonArray.getClass()).newInstance(player, pixelmonArray);
               cEntity = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity6CanBattle");
               c = Class.forName("com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant");
               m = cEntity.getMethod("StartBattle", c, c);
               m.invoke(pixelmon, parTrainer, parPlayer);
               return true;
            }
         } catch (Exception var11) {
            LogManager.getLogger().error("startBattle", var11);
            return false;
         }
      }
   }

   public static int countPCPixelmon(EntityPlayerMP player) {
      try {
         Object ob = getPlayerComputerStorage.invoke(player);
         return ((Integer)ob.getClass().getMethod("count").invoke(ob)).intValue();
      } catch (Exception var2) {
         var2.printStackTrace();
         return 0;
      }
   }

   public static String getAttackName(int id) {
      try {
         Object ob = attackByID.newInstance(id);
         return ob == null ? null : getAttackName.get(baseAttack.get(ob)) + "";
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static int getAttackID(String name) {
      try {
         Object ob = attackByName.newInstance(name);
         return ob == null ? -1 : getAttackName.getInt(baseAttack.get(ob));
      } catch (Exception var2) {
         var2.printStackTrace();
         return -1;
      }
   }
}
