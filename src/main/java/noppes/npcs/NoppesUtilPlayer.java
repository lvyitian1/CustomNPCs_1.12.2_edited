package noppes.npcs;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.oredict.OreDictionary;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.event.QuestEvent$QuestTurnedInEvent;
import noppes.npcs.api.event.RoleEvent$BankUnlockedEvent;
import noppes.npcs.api.event.RoleEvent$BankUpgradedEvent;
import noppes.npcs.api.event.RoleEvent$FollowerHireEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.BankData;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleDialog;
import noppes.npcs.roles.RoleFollower;

public class NoppesUtilPlayer {
   public static void changeFollowerState(EntityPlayerMP player, EntityNPCInterface npc) {
      if (npc.advanced.role == 2) {
         RoleFollower role = (RoleFollower)npc.roleInterface;
         EntityPlayer owner = role.owner;
         if (owner != null && owner.getName().equals(player.getName())) {
            role.isFollowing = !role.isFollowing;
         }
      }
   }

   public static void hireFollower(EntityPlayerMP player, EntityNPCInterface npc) {
      if (npc.advanced.role == 2) {
         Container con = player.openContainer;
         if (con != null && con instanceof ContainerNPCFollowerHire) {
            ContainerNPCFollowerHire container = (ContainerNPCFollowerHire)con;
            RoleFollower role = (RoleFollower)npc.roleInterface;
            followerBuy(role, container.currencyMatrix, player, npc);
         }
      }
   }

   public static void extendFollower(EntityPlayerMP player, EntityNPCInterface npc) {
      if (npc.advanced.role == 2) {
         Container con = player.openContainer;
         if (con != null && con instanceof ContainerNPCFollower) {
            ContainerNPCFollower container = (ContainerNPCFollower)con;
            RoleFollower role = (RoleFollower)npc.roleInterface;
            followerBuy(role, container.currencyMatrix, player, npc);
         }
      }
   }

   public static void teleportPlayer(EntityPlayerMP player, double x, double y, double z, int dimension) {
      if (player.dimension != dimension) {
         int dim = player.dimension;
         MinecraftServer server = player.getServer();
         WorldServer wor = server.getWorld(dimension);
         if (wor == null) {
            player.sendMessage(new TextComponentString("Broken transporter. Dimenion does not exist"));
            return;
         }

         player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
         server.getPlayerList().transferPlayerToDimension(player, dimension, new CustomTeleporter(wor));
         player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
         if (!wor.playerEntities.contains(player)) {
            wor.spawnEntity(player);
         }
      } else {
         player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
      }

      player.world.updateEntityWithOptionalForce(player, false);
   }

   private static void followerBuy(RoleFollower role, IInventory currencyInv, EntityPlayerMP player, EntityNPCInterface npc) {
      ItemStack currency = currencyInv.getStackInSlot(0);
      if (currency != null && !currency.isEmpty()) {
         HashMap<ItemStack, Integer> cd = new HashMap();

         for(int slot = 0; slot < role.inventory.items.size(); ++slot) {
            ItemStack is = (ItemStack)role.inventory.items.get(slot);
            if (!is.isEmpty() && is.getItem() == currency.getItem() && (!is.getHasSubtypes() || is.getItemDamage() == currency.getItemDamage())) {
               int days = 1;
               if (role.rates.containsKey(Integer.valueOf(slot))) {
                  days = ((Integer)role.rates.get(Integer.valueOf(slot))).intValue();
               }

               cd.put(is, Integer.valueOf(days));
            }
         }

         if (cd.size() != 0) {
            int stackSize = currency.getCount();
            int days = 0;
            int possibleDays = 0;
            int possibleSize = stackSize;

            while(true) {
               for(ItemStack item : cd.keySet()) {
                  int rDays = ((Integer)cd.get(item)).intValue();
                  int rValue = item.getCount();
                  if (rValue <= stackSize) {
                     int newStackSize = stackSize % rValue;
                     int size = stackSize - newStackSize;
                     int posDays = size / rValue * rDays;
                     if (possibleDays <= posDays) {
                        possibleDays = posDays;
                        possibleSize = newStackSize;
                     }
                  }
               }

               if (stackSize == possibleSize) {
                  RoleEvent$FollowerHireEvent event = new RoleEvent$FollowerHireEvent(player, npc.wrappedNPC, days);
                  if (EventHooks.onNPCRole(npc, event)) {
                     return;
                  }

                  if (event.days == 0) {
                     return;
                  }

                  if (stackSize <= 0) {
                     currencyInv.setInventorySlotContents(0, ItemStack.EMPTY);
                  } else {
                     currency.splitStack(stackSize);
                  }

                  npc.say(player, new Line(NoppesStringUtils.formatText(role.dialogHire.replace("{days}", days + ""), player, npc)));
                  role.setOwner(player);
                  role.addDays(days);
                  return;
               }

               stackSize = possibleSize;
               days += possibleDays;
               possibleDays = 0;
            }
         }
      }
   }

   public static void bankUpgrade(EntityPlayerMP player, EntityNPCInterface npc) {
      if (npc.advanced.role == 3) {
         Container con = player.openContainer;
         if (con != null && con instanceof ContainerNPCBankInterface) {
            ContainerNPCBankInterface container = (ContainerNPCBankInterface)con;
            Bank bank = BankController.getInstance().getBank(container.bankid);
            ItemStack item = bank.upgradeInventory.getStackInSlot(container.slot);
            if (item != null && !item.isEmpty()) {
               int price = item.getCount();
               ItemStack currency = container.currencyMatrix.getStackInSlot(0);
               if (currency != null && !currency.isEmpty() && price <= currency.getCount()) {
                  if (currency.getCount() - price == 0) {
                     container.currencyMatrix.setInventorySlotContents(0, ItemStack.EMPTY);
                  } else {
                     currency.splitStack(price);
                  }

                  player.closeContainer();
                  PlayerBankData data = PlayerDataController.instance.getBankData(player, bank.id);
                  BankData bankData = data.getBank(bank.id);
                  bankData.upgradedSlots.put(Integer.valueOf(container.slot), Boolean.valueOf(true));
                  RoleEvent$BankUpgradedEvent event = new RoleEvent$BankUpgradedEvent(player, npc.wrappedNPC, container.slot);
                  EventHooks.onNPCRole(npc, event);
                  bankData.openBankGui(player, npc, bank.id, container.slot);
               }
            }
         }
      }
   }

   public static void bankUnlock(EntityPlayerMP player, EntityNPCInterface npc) {
      if (npc.advanced.role == 3) {
         Container con = player.openContainer;
         if (con != null && con instanceof ContainerNPCBankInterface) {
            ContainerNPCBankInterface container = (ContainerNPCBankInterface)con;
            Bank bank = BankController.getInstance().getBank(container.bankid);
            ItemStack item = bank.currencyInventory.getStackInSlot(container.slot);
            if (item != null && !item.isEmpty()) {
               int price = item.getCount();
               ItemStack currency = container.currencyMatrix.getStackInSlot(0);
               if (currency != null && !currency.isEmpty() && price <= currency.getCount()) {
                  if (currency.getCount() - price == 0) {
                     container.currencyMatrix.setInventorySlotContents(0, ItemStack.EMPTY);
                  } else {
                     currency.splitStack(price);
                  }

                  player.closeContainer();
                  PlayerBankData data = PlayerDataController.instance.getBankData(player, bank.id);
                  BankData bankData = data.getBank(bank.id);
                  if (bankData.unlockedSlots + 1 <= bank.maxSlots) {
                     ++bankData.unlockedSlots;
                  }

                  RoleEvent$BankUnlockedEvent event = new RoleEvent$BankUnlockedEvent(player, npc.wrappedNPC, container.slot);
                  EventHooks.onNPCRole(npc, event);
                  bankData.openBankGui(player, npc, bank.id, container.slot);
               }
            }
         }
      }
   }

   public static void sendData(EnumPlayerPacket enu, Object... obs) {
      PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

      try {
         if (!Server.fillBuffer(buffer, enu, obs)) {
            return;
         }

         CustomNpcs.ChannelPlayer.sendToServer(new FMLProxyPacket(buffer, "CustomNPCsPlayer"));
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public static void dialogSelected(int dialogId, int optionId, EntityPlayerMP player, EntityNPCInterface npc) {
      if (dialogId < 0 && npc.advanced.role == 7) {
         String text = (String)((RoleDialog)npc.roleInterface).optionsTexts.get(Integer.valueOf(optionId));
         if (text != null && !text.isEmpty()) {
            Dialog d = new Dialog((DialogCategory)null);
            d.text = text;
            NoppesUtilServer.openDialog(player, npc, d);
         }

      } else {
         Dialog dialog = (Dialog)DialogController.instance.dialogs.get(Integer.valueOf(dialogId));
         if (dialog != null) {
            EventHooks.onNPCDialogClose(npc, player, dialog);
            if (dialog.hasDialogs(player) || dialog.hasOtherOptions()) {
               DialogOption option = (DialogOption)dialog.options.get(Integer.valueOf(optionId));
               if (option != null) {
                  if (EventHooks.onNPCDialogOption(npc, player, dialog, option)) {
                     Server.sendData(player, EnumPacketClient.GUI_CLOSE, Integer.valueOf(-1), new NBTTagCompound());
                     EventHooks.onNPCDialogClose(npc, player, dialog);
                  } else if ((option.optionType != 1 || option.isAvailable(player) && option.hasDialog()) && option.optionType != 2 && option.optionType != 0) {
                     if (option.optionType == 3) {
                        if (npc.roleInterface != null) {
                           if (npc.advanced.role == 6) {
                              ((RoleCompanion)npc.roleInterface).interact(player, true);
                           } else {
                              npc.roleInterface.interact(player);
                           }
                        } else {
                           Server.sendData(player, EnumPacketClient.GUI_CLOSE, Integer.valueOf(-1), new NBTTagCompound());
                        }
                     } else if (option.optionType == 1) {
                        NoppesUtilServer.openDialog(player, npc, option.getDialog());
                     } else if (option.optionType == 4) {
                        Server.sendData(player, EnumPacketClient.GUI_CLOSE, Integer.valueOf(-1), new NBTTagCompound());
                        NoppesUtilServer.runCommand(npc, npc.getName(), option.command, player);
                     } else {
                        Server.sendData(player, EnumPacketClient.GUI_CLOSE, Integer.valueOf(-1), new NBTTagCompound());
                     }

                  }
               }
            }
         }
      }
   }

   public static void questCompletion(EntityPlayerMP player, int questId) {
      PlayerData data = PlayerData.get(player);
      PlayerQuestData playerdata = data.questData;
      QuestData questdata = (QuestData)playerdata.activeQuests.get(Integer.valueOf(questId));
      if (questdata != null) {
         Quest quest = questdata.quest;
         if (quest.questInterface.isCompleted(player)) {
            QuestEvent$QuestTurnedInEvent event = new QuestEvent$QuestTurnedInEvent(data.scriptData.getPlayer(), quest);
            event.expReward = quest.rewardExp;
            List<IItemStack> list = new ArrayList();

            for(ItemStack item : quest.rewardItems.items) {
               if (!item.isEmpty()) {
                  list.add(NpcAPI.Instance().getIItemStack(item));
               }
            }

            if (!quest.randomReward) {
               event.itemRewards = (IItemStack[])list.toArray(new IItemStack[list.size()]);
            } else if (!list.isEmpty()) {
               event.itemRewards = new IItemStack[]{(IItemStack)list.get(player.getRNG().nextInt(list.size()))};
            }

            EventHooks.onQuestTurnedIn(data.scriptData, event);

            for(IItemStack item : event.itemRewards) {
               if (item != null) {
                  NoppesUtilServer.GivePlayerItem(player, player, item.getMCItemStack());
               }
            }

            quest.questInterface.handleComplete(player);
            if (event.expReward > 0) {
               NoppesUtilServer.playSound(player, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.8F));
               player.addExperience(event.expReward);
            }

            quest.factionOptions.addPoints(player);
            if (quest.mail.isValid()) {
               PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName(), quest.mail);
            }

            if (!quest.command.isEmpty()) {
               NoppesUtilServer.runCommand(player, "QuestCompletion", quest.command, player);
            }

            PlayerQuestController.setQuestFinished(quest, player);
            if (quest.hasNewQuest()) {
               PlayerQuestController.addActiveQuest(quest.getNextQuest(), player);
            }

         }
      }
   }

   public static boolean compareItems(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
      if (!NoppesUtilServer.IsItemStackNull(item) && !NoppesUtilServer.IsItemStackNull(item2)) {
         boolean oreMatched = false;
         OreDictionary.itemMatches(item, item2, false);
         int[] ids = OreDictionary.getOreIDs(item);
         if (ids.length > 0) {
            for(int id : ids) {
               boolean match1 = false;
               boolean match2 = false;

               for(ItemStack is : OreDictionary.getOres(OreDictionary.getOreName(id))) {
                  if (compareItemDetails(item, is, ignoreDamage, ignoreNBT)) {
                     match1 = true;
                  }

                  if (compareItemDetails(item2, is, ignoreDamage, ignoreNBT)) {
                     match2 = true;
                  }
               }

               if (match1 && match2) {
                  return true;
               }
            }
         }

         return compareItemDetails(item, item2, ignoreDamage, ignoreNBT);
      } else {
         return false;
      }
   }

   private static boolean compareItemDetails(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
      if (item.getItem() != item2.getItem()) {
         return false;
      } else if (!ignoreDamage && item.getItemDamage() != -1 && item.getItemDamage() != item2.getItemDamage()) {
         return false;
      } else if (ignoreNBT || item.getTagCompound() == null || item2.getTagCompound() != null && item.getTagCompound().equals(item2.getTagCompound())) {
         return ignoreNBT || item2.getTagCompound() == null || item.getTagCompound() != null;
      } else {
         return false;
      }
   }

   public static boolean compareItems(EntityPlayer player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
      int size = 0;

      for(ItemStack is : player.inventory.mainInventory) {
         if (!NoppesUtilServer.IsItemStackNull(is) && compareItems(item, is, ignoreDamage, ignoreNBT)) {
            size += is.getCount();
         }
      }

      return size >= item.getCount();
   }

   public static void consumeItem(EntityPlayer player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
      if (!NoppesUtilServer.IsItemStackNull(item)) {
         int size = item.getCount();

         for(int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            ItemStack is = (ItemStack)player.inventory.mainInventory.get(i);
            if (!NoppesUtilServer.IsItemStackNull(is) && compareItems(item, is, ignoreDamage, ignoreNBT)) {
               if (size < is.getCount()) {
                  is.splitStack(size);
                  break;
               }

               size -= is.getCount();
               player.inventory.mainInventory.set(i, ItemStack.EMPTY);
            }
         }

      }
   }

   public static List<ItemStack> countStacks(IInventory inv, boolean ignoreDamage, boolean ignoreNBT) {
      List<ItemStack> list = new ArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack item = inv.getStackInSlot(i);
         if (!NoppesUtilServer.IsItemStackNull(item)) {
            boolean found = false;

            for(ItemStack is : list) {
               if (compareItems(item, is, ignoreDamage, ignoreNBT)) {
                  is.setCount(is.getCount() + item.getCount());
                  found = true;
                  break;
               }
            }

            if (!found) {
               list.add(item.copy());
            }
         }
      }

      return list;
   }
}
