package noppes.npcs;

import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Post;
import net.minecraftforge.event.world.ChunkDataEvent.Save;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.WrapperEntityData;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemSoulstoneEmpty;
import noppes.npcs.quests.QuestKill;
import noppes.npcs.roles.RoleFollower;

public class ServerEventsHandler {
   public static EntityVillager Merchant;
   public static Entity mounted;

   @SubscribeEvent
   public void invoke(EntityInteract event) {
      ItemStack item = event.getEntityPlayer().getHeldItemMainhand();
      if (item != null) {
         boolean isRemote = event.getEntityPlayer().world.isRemote;
         boolean npcInteracted = event.getTarget() instanceof EntityNPCInterface;
         if (isRemote || !CustomNpcs.OpsOnly || event.getEntityPlayer().getServer().getPlayerList().canSendCommands(event.getEntityPlayer().getGameProfile())) {
            if (!isRemote && item.getItem() == CustomItems.soulstoneEmpty && event.getTarget() instanceof EntityLivingBase) {
               ((ItemSoulstoneEmpty)item.getItem()).store((EntityLivingBase)event.getTarget(), item, event.getEntityPlayer());
            }

            if (item.getItem() == CustomItems.wand && npcInteracted && !isRemote) {
               CustomNpcsPermissions var11 = CustomNpcsPermissions.Instance;
               if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.NPC_GUI)) {
                  return;
               }

               event.setCanceled(true);
               NoppesUtilServer.sendOpenGui(event.getEntityPlayer(), EnumGuiType.MainMenuDisplay, (EntityNPCInterface)event.getTarget());
            } else if (item.getItem() == CustomItems.cloner && !isRemote && !(event.getTarget() instanceof EntityPlayer)) {
               NBTTagCompound compound = new NBTTagCompound();
               if (!event.getTarget().writeToNBTOptional(compound)) {
                  return;
               }

               PlayerData data = PlayerData.get(event.getEntityPlayer());
               ServerCloneController.Instance.cleanTags(compound);
               if (!Server.sendDataChecked((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.CLONE, compound)) {
                  event.getEntityPlayer().sendMessage(new TextComponentString("Entity too big to clone"));
               }

               data.cloned = compound;
               event.setCanceled(true);
            } else if (item.getItem() == CustomItems.scripter && !isRemote && npcInteracted) {
               CustomNpcsPermissions var10 = CustomNpcsPermissions.Instance;
               if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.NPC_GUI)) {
                  return;
               }

               NoppesUtilServer.setEditingNpc(event.getEntityPlayer(), (EntityNPCInterface)event.getTarget());
               event.setCanceled(true);
               Server.sendData((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.GUI, EnumGuiType.Script.ordinal(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            } else if (item.getItem() == CustomItems.mount) {
               CustomNpcsPermissions var10000 = CustomNpcsPermissions.Instance;
               if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.TOOL_MOUNTER)) {
                  return;
               }

               event.setCanceled(true);
               mounted = event.getTarget();
               if (isRemote) {
                  CustomNpcs.proxy.openGui(MathHelper.floor(mounted.posX), MathHelper.floor(mounted.posY), MathHelper.floor(mounted.posZ), EnumGuiType.MobSpawnerMounter, event.getEntityPlayer());
               }
            } else if (item.getItem() == CustomItems.wand && event.getTarget() instanceof EntityVillager) {
               CustomNpcsPermissions var9 = CustomNpcsPermissions.Instance;
               if (!CustomNpcsPermissions.hasPermission(event.getEntityPlayer(), CustomNpcsPermissions.EDIT_VILLAGER)) {
                  return;
               }

               event.setCanceled(true);
               Merchant = (EntityVillager)event.getTarget();
               if (!isRemote) {
                  EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
                  player.openGui(CustomNpcs.instance, EnumGuiType.MerchantAdd.ordinal(), player.world, 0, 0, 0);
                  MerchantRecipeList merchantrecipelist = Merchant.getRecipes(player);
                  if (merchantrecipelist != null) {
                     Server.sendData(player, EnumPacketClient.VILLAGER_LIST, merchantrecipelist);
                  }
               }
            }

         }
      }
   }

   @SubscribeEvent
   public void invoke(LivingDeathEvent event) {
      if (!event.getEntityLiving().world.isRemote) {
         Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
         if (source != null) {
            if (source instanceof EntityNPCInterface && event.getEntityLiving() != null) {
               EntityNPCInterface npc = (EntityNPCInterface)source;
               Line line = npc.advanced.getKillLine();
               if (line != null) {
                  npc.saySurrounding(line.formatTarget(event.getEntityLiving()));
               }

               EventHooks.onNPCKills(npc, event.getEntityLiving());
            }

            EntityPlayer player = null;
            if (source instanceof EntityPlayer) {
               player = (EntityPlayer)source;
            } else if (source instanceof EntityNPCInterface && ((EntityNPCInterface)source).advanced.role == 2) {
               player = ((RoleFollower)((EntityNPCInterface)source).roleInterface).owner;
            }

            if (player != null) {
               this.doQuest(player, event.getEntityLiving(), true);
               if (event.getEntityLiving() instanceof EntityNPCInterface) {
                  this.doFactionPoints(player, (EntityNPCInterface)event.getEntityLiving());
               }
            }
         }

         if (event.getEntityLiving() instanceof EntityPlayer) {
            PlayerData data = PlayerData.get((EntityPlayer)event.getEntityLiving());
            data.save(false);
         }

      }
   }

   private void doFactionPoints(EntityPlayer player, EntityNPCInterface npc) {
      npc.advanced.factions.addPoints(player);
   }

   private void doQuest(EntityPlayer player, EntityLivingBase entity, boolean all) {
      PlayerData pdata = PlayerData.get(player);
      PlayerQuestData playerdata = pdata.questData;
      String entityName = EntityList.getEntityString(entity);
      Iterator var7 = playerdata.activeQuests.values().iterator();

      while(true) {
         QuestData data;
         String name;
         QuestKill quest;
         while(true) {
            if (!var7.hasNext()) {
               playerdata.checkQuestCompletion(player, 2);
               return;
            }

            data = (QuestData)var7.next();
            if (data.quest.type == 2 || data.quest.type == 4) {
               if (data.quest.type == 4 && all) {
                  for(EntityPlayer pl : player.world.getEntitiesWithinAABB(EntityPlayer.class, entity.getEntityBoundingBox().grow(10.0D, 10.0D, 10.0D))) {
                     if (pl != player) {
                        this.doQuest(pl, entity, false);
                     }
                  }
               }

               name = entityName;
               quest = (QuestKill)data.quest.questInterface;
               if (quest.targets.containsKey(entity.getName())) {
                  name = entity.getName();
                  break;
               }

               if (quest.targets.containsKey(entityName)) {
                  break;
               }
            }
         }

         HashMap<String, Integer> killed = quest.getKilled(data);
         if (!killed.containsKey(name) || ((Integer)killed.get(name)).intValue() < ((Integer)quest.targets.get(name)).intValue()) {
            int amount = 0;
            if (killed.containsKey(name)) {
               amount = ((Integer)killed.get(name)).intValue();
            }

            killed.put(name, Integer.valueOf(amount + 1));
            quest.setKilled(data, killed);
            pdata.updateClient = true;
         }
      }
   }

   @SubscribeEvent
   public void pickUp(EntityItemPickupEvent event) {
      if (!event.getEntityPlayer().world.isRemote) {
         PlayerQuestData playerdata = PlayerData.get(event.getEntityPlayer()).questData;
         playerdata.checkQuestCompletion(event.getEntityPlayer(), 0);
      }
   }

   @SubscribeEvent
   public void world(EntityJoinWorldEvent event) {
      if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
         PlayerData data = PlayerData.get((EntityPlayer)event.getEntity());
         data.updateCompanion(event.getWorld());
      }
   }

   @SubscribeEvent
   public void populateChunk(Post event) {
      NPCSpawning.performWorldGenSpawning(event.getWorld(), event.getChunkX(), event.getChunkZ(), event.getRand());
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void attachEntity(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof EntityPlayer) {
         PlayerData.register(event);
      }

      if (event.getObject() instanceof EntityLivingBase) {
         MarkData.register(event);
      }

      if (((Entity)event.getObject()).world != null && !((Entity)event.getObject()).world.isRemote && ((Entity)event.getObject()).world instanceof WorldServer) {
         WrapperEntityData.register(event);
      }

   }

   @SubscribeEvent
   public void attachItem(AttachCapabilitiesEvent<ItemStack> event) {
      ItemStackWrapper.register(event);
   }

   @SubscribeEvent
   public void savePlayer(SaveToFile event) {
      PlayerData.get(event.getEntityPlayer()).save(false);
   }

   @SubscribeEvent
   public void saveChunk(Save event) {
      ClassInheritanceMultiMap[] var2 = event.getChunk().getEntityLists();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         //TODO: LikeWind
         for(Object e1 : var2[var4]) {
            Entity e = (Entity)e1;
            if (e instanceof EntityLivingBase) {
               MarkData.get((EntityLivingBase)e).save();
            }
         }
      }

   }

   @SubscribeEvent
   public void playerTracking(StartTracking event) {
      if (event.getTarget() instanceof EntityLivingBase && !event.getTarget().world.isRemote) {
         MarkData data = MarkData.get((EntityLivingBase)event.getTarget());
         if (!data.marks.isEmpty()) {
            Server.sendData((EntityPlayerMP)event.getEntityPlayer(), EnumPacketClient.MARK_DATA, event.getTarget().getEntityId(), data.getNBT());
         }
      }
   }
}
