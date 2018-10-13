package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Close;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.event.ForgeEvent;
import noppes.npcs.api.event.ItemEvent$AttackEvent;
import noppes.npcs.api.event.ItemEvent$InteractEvent;
import noppes.npcs.api.event.PlayerEvent$AttackEvent;
import noppes.npcs.api.event.PlayerEvent$BreakEvent;
import noppes.npcs.api.event.PlayerEvent$ChatEvent;
import noppes.npcs.api.event.PlayerEvent$DamagedEntityEvent;
import noppes.npcs.api.event.PlayerEvent$DamagedEvent;
import noppes.npcs.api.event.PlayerEvent$InteractEvent;
import noppes.npcs.api.event.PlayerEvent$RangedLaunchedEvent;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.ItemScripted;

public class ScriptPlayerEventHandler {
   @SubscribeEvent
   public void onServerTick(PlayerTickEvent event) {
      if (event.side == Side.SERVER && event.phase == Phase.START) {
         PlayerData data = PlayerData.get(event.player);
         if (event.player.ticksExisted % 10 == 0) {
            EventHooks.onPlayerTick(data.scriptData);

            for(int i = 0; i < event.player.inventory.getSizeInventory(); ++i) {
               ItemStack item = event.player.inventory.getStackInSlot(i);
               if (!item.isEmpty() && item.getItem() == CustomItems.scripted_item) {
                  ItemScriptedWrapper isw = (ItemScriptedWrapper)NpcAPI.Instance().getIItemStack(item);
                  EventHooks.onScriptItemUpdate(isw, event.player);
                  if (isw.updateClient) {
                     isw.updateClient = false;
                     Server.sendData((EntityPlayerMP)event.player, EnumPacketClient.UPDATE_ITEM, i, isw.getNBT());
                  }
               }
            }
         }

         if (data.playerLevel != event.player.experienceLevel) {
            EventHooks.onPlayerLevelUp(data.scriptData, data.playerLevel - event.player.experienceLevel);
            data.playerLevel = event.player.experienceLevel;
         }

         data.timers.update();
         if (data.updateClient) {
            Server.sendData((EntityPlayerMP)event.player, EnumPacketClient.SYNC_END, Integer.valueOf(8), data.getSyncNBT());
            data.updateClient = false;
         }
      }

   }

   @SubscribeEvent
   public void invoke(LeftClickBlock event) {
      if (!event.getEntityPlayer().world.isRemote && event.getHand() == EnumHand.MAIN_HAND) {
         PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
         PlayerEvent$AttackEvent ev = new PlayerEvent$AttackEvent(handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(event.getWorld(), event.getPos()));
         event.setCanceled(EventHooks.onPlayerAttack(handler, ev));
         if (event.getItemStack().getItem() == CustomItems.scripted_item && !event.isCanceled()) {
            ItemScriptedWrapper isw = ItemScripted.GetWrapper(event.getItemStack());
            ItemEvent$AttackEvent eve = new ItemEvent$AttackEvent(isw, handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(event.getWorld(), event.getPos()));
            eve.setCanceled(event.isCanceled());
            event.setCanceled(EventHooks.onScriptItemAttack(isw, eve));
         }

      }
   }

   @SubscribeEvent
   public void invoke(RightClickBlock event) {
      if (!event.getEntityPlayer().world.isRemote && event.getHand() == EnumHand.MAIN_HAND && event.getWorld() instanceof WorldServer) {
         PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
         handler.hadInteract = true;
         PlayerEvent$InteractEvent ev = new PlayerEvent$InteractEvent(handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(event.getWorld(), event.getPos()));
         event.setCanceled(EventHooks.onPlayerInteract(handler, ev));
         if (event.getItemStack().getItem() == CustomItems.scripted_item && !event.isCanceled()) {
            ItemScriptedWrapper isw = ItemScripted.GetWrapper(event.getItemStack());
            ItemEvent$InteractEvent eve = new ItemEvent$InteractEvent(isw, handler.getPlayer(), 2, NpcAPI.Instance().getIBlock(event.getWorld(), event.getPos()));
            event.setCanceled(EventHooks.onScriptItemInteract(isw, eve));
         }

      }
   }

   @SubscribeEvent
   public void invoke(EntityInteract event) {
      if (!event.getEntityPlayer().world.isRemote && event.getHand() == EnumHand.MAIN_HAND) {
         PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
         PlayerEvent$InteractEvent ev = new PlayerEvent$InteractEvent(handler.getPlayer(), 1, NpcAPI.Instance().getIEntity(event.getTarget()));
         event.setCanceled(EventHooks.onPlayerInteract(handler, ev));
         if (event.getItemStack().getItem() == CustomItems.scripted_item && !event.isCanceled()) {
            ItemScriptedWrapper isw = ItemScripted.GetWrapper(event.getItemStack());
            ItemEvent$InteractEvent eve = new ItemEvent$InteractEvent(isw, handler.getPlayer(), 1, NpcAPI.Instance().getIEntity(event.getTarget()));
            event.setCanceled(EventHooks.onScriptItemInteract(isw, eve));
         }

      }
   }

   @SubscribeEvent
   public void invoke(LeftClickEmpty event) {
      if (event.getHand() == EnumHand.MAIN_HAND) {
         NoppesUtilPlayer.sendData(EnumPlayerPacket.LeftClick);
      }
   }

   @SubscribeEvent
   public void invoke(RightClickItem event) {
      if (!event.getEntityPlayer().world.isRemote && event.getHand() == EnumHand.MAIN_HAND) {
         if (event.getEntityPlayer().isCreative() && event.getEntityPlayer().isSneaking() && event.getItemStack().getItem() == CustomItems.scripted_item) {
            NoppesUtilServer.sendOpenGui(event.getEntityPlayer(), EnumGuiType.ScriptItem, (EntityNPCInterface)null);
         } else {
            PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
            if (handler.hadInteract) {
               handler.hadInteract = false;
            } else {
               PlayerEvent$InteractEvent ev = new PlayerEvent$InteractEvent(handler.getPlayer(), 0, (Object)null);
               event.setCanceled(EventHooks.onPlayerInteract(handler, ev));
               if (event.getItemStack().getItem() == CustomItems.scripted_item && !event.isCanceled()) {
                  ItemScriptedWrapper isw = ItemScripted.GetWrapper(event.getItemStack());
                  ItemEvent$InteractEvent eve = new ItemEvent$InteractEvent(isw, handler.getPlayer(), 0, (Object)null);
                  event.setCanceled(EventHooks.onScriptItemInteract(isw, eve));
               }

            }
         }
      }
   }

   @SubscribeEvent
   public void invoke(ArrowLooseEvent event) {
      if (!event.getEntityPlayer().world.isRemote) {
         PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
         PlayerEvent$RangedLaunchedEvent ev = new PlayerEvent$RangedLaunchedEvent(handler.getPlayer());
         event.setCanceled(EventHooks.onPlayerRanged(handler, ev));
      }
   }

   @SubscribeEvent
   public void invoke(BreakEvent event) {
      if (!event.getPlayer().world.isRemote) {
         PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
         PlayerEvent$BreakEvent ev = new PlayerEvent$BreakEvent(handler.getPlayer(), NpcAPI.Instance().getIBlock(event.getWorld(), event.getPos()), event.getExpToDrop());
         event.setCanceled(EventHooks.onPlayerBreak(handler, ev));
         event.setExpToDrop(ev.exp);
      }
   }

   @SubscribeEvent
   public void invoke(ItemTossEvent event) {
      if (!event.getPlayer().world.isRemote) {
         PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
         event.setCanceled(EventHooks.onPlayerToss(handler, event.getEntityItem()));
      }
   }

   @SubscribeEvent
   public void invoke(EntityItemPickupEvent event) {
      if (!event.getEntityPlayer().world.isRemote) {
         PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
         event.setCanceled(EventHooks.onPlayerPickUp(handler, event.getItem()));
      }
   }

   @SubscribeEvent
   public void invoke(Open event) {
      if (!event.getEntityPlayer().world.isRemote) {
         PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
         EventHooks.onPlayerContainerOpen(handler, event.getContainer());
      }
   }

   @SubscribeEvent
   public void invoke(Close event) {
      if (!event.getEntityPlayer().world.isRemote) {
         PlayerScriptData handler = PlayerData.get(event.getEntityPlayer()).scriptData;
         EventHooks.onPlayerContainerClose(handler, event.getContainer());
      }
   }

   @SubscribeEvent
   public void invoke(LivingDeathEvent event) {
      if (!event.getEntityLiving().world.isRemote) {
         Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
         if (event.getEntityLiving() instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer)event.getEntityLiving()).scriptData;
            EventHooks.onPlayerDeath(handler, event.getSource(), source);
         }

         if (source instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer)source).scriptData;
            EventHooks.onPlayerKills(handler, event.getEntityLiving());
         }

      }
   }

   @SubscribeEvent
   public void invoke(LivingHurtEvent event) {
      if (!event.getEntityLiving().world.isRemote) {
         Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
         if (event.getEntityLiving() instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer)event.getEntityLiving()).scriptData;
            PlayerEvent$DamagedEvent pevent = new PlayerEvent$DamagedEvent(handler.getPlayer(), source, event.getAmount(), event.getSource());
            event.setCanceled(EventHooks.onPlayerDamaged(handler, pevent));
            event.setAmount(pevent.damage);
         }

         if (source instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer)source).scriptData;
            PlayerEvent$DamagedEntityEvent pevent = new PlayerEvent$DamagedEntityEvent(handler.getPlayer(), event.getEntityLiving(), event.getAmount(), event.getSource());
            event.setCanceled(EventHooks.onPlayerDamagedEntity(handler, pevent));
            event.setAmount(pevent.damage);
         }

      }
   }

   @SubscribeEvent
   public void invoke(LivingAttackEvent event) {
      if (!event.getEntityLiving().world.isRemote) {
         Entity source = NoppesUtilServer.GetDamageSourcee(event.getSource());
         if (source instanceof EntityPlayer) {
            PlayerScriptData handler = PlayerData.get((EntityPlayer)source).scriptData;
            ItemStack item = ((EntityPlayer)source).getHeldItemMainhand();
            IEntity target = NpcAPI.Instance().getIEntity(event.getEntityLiving());
            PlayerEvent$AttackEvent ev = new PlayerEvent$AttackEvent(handler.getPlayer(), 1, target);
            event.setCanceled(EventHooks.onPlayerAttack(handler, ev));
            if (item.getItem() == CustomItems.scripted_item && !event.isCanceled()) {
               ItemScriptedWrapper isw = ItemScripted.GetWrapper(item);
               ItemEvent$AttackEvent eve = new ItemEvent$AttackEvent(isw, handler.getPlayer(), 1, target);
               eve.setCanceled(event.isCanceled());
               event.setCanceled(EventHooks.onScriptItemAttack(isw, eve));
            }
         }

      }
   }

   @SubscribeEvent
   public void invoke(PlayerLoggedInEvent event) {
      if (!event.player.world.isRemote) {
         PlayerScriptData handler = PlayerData.get(event.player).scriptData;
         EventHooks.onPlayerLogin(handler);
      }
   }

   @SubscribeEvent
   public void invoke(PlayerLoggedOutEvent event) {
      if (!event.player.world.isRemote) {
         PlayerScriptData handler = PlayerData.get(event.player).scriptData;
         EventHooks.onPlayerLogout(handler);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void invoke(ServerChatEvent event) {
      if (!event.getPlayer().world.isRemote && event.getPlayer() != EntityNPCInterface.ChatEventPlayer) {
         PlayerScriptData handler = PlayerData.get(event.getPlayer()).scriptData;
         String message = event.getMessage();
         PlayerEvent$ChatEvent ev = new PlayerEvent$ChatEvent(handler.getPlayer(), event.getMessage());
         EventHooks.onPlayerChat(handler, ev);
         event.setCanceled(ev.isCanceled());
         if (!message.equals(ev.message)) {
            TextComponentTranslation chat = new TextComponentTranslation("", new Object[0]);
            chat.appendSibling(ForgeHooks.newChatWithLinks(ev.message));
            event.setComponent(chat);
         }

      }
   }

   @SubscribeEvent
   public void forgeEntity(Event event) {
      if (CustomNpcs.Server != null && !(event instanceof GenericEvent) && !(event instanceof InputEvent) && !(event instanceof PotentialSpawns) && !event.getClass().getName().startsWith("net.minecraftforge.client") && !event.getClass().getName().startsWith("net.minecraftforge.fml.client") && !event.getClass().getName().startsWith("net.minecraftforge.event.terraingen")) {
         if (!(event instanceof TickEvent) || ((TickEvent)event).side != Side.CLIENT) {
            if (event instanceof EntityEvent) {
               EntityEvent ev = (EntityEvent)event;
               if (ev.getEntity() != null && ev.getEntity().world instanceof WorldServer && !(event instanceof EntityConstructing)) {
                  EventHooks.onForgeEntityEvent(ev);
               }
            } else if (event instanceof WorldEvent) {
               WorldEvent ev = (WorldEvent)event;
               if (ev.getWorld() instanceof WorldServer) {
                  EventHooks.onForgeWorldEvent(ev);
               }
            } else {
               EventHooks.onForgeEvent(new ForgeEvent(event), event);
            }
         }
      }
   }
}
