package noppes.npcs;

import io.netty.buffer.ByteBuf;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.containers.ContainerManageRecipes;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerDialogData;
import noppes.npcs.controllers.data.PlayerFactionData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleTransporter;
import noppes.npcs.util.CustomNPCsScheduler;

public class NoppesUtilServer {
   private static HashMap<String, Quest> editingQuests = new HashMap();

   public static void setEditingNpc(EntityPlayer player, EntityNPCInterface npc) {
      PlayerData data = PlayerData.get(player);
      data.editingNpc = npc;
      if (npc != null) {
         Server.sendDataChecked((EntityPlayerMP)player, EnumPacketClient.EDIT_NPC, npc.getEntityId());
      }

   }

   public static EntityNPCInterface getEditingNpc(EntityPlayer player) {
      PlayerData data = PlayerData.get(player);
      return data.editingNpc;
   }

   public static void setEditingQuest(EntityPlayer player, Quest quest) {
      editingQuests.put(player.getName(), quest);
   }

   public static Quest getEditingQuest(EntityPlayer player) {
      return (Quest)editingQuests.get(player.getName());
   }

   public static void sendRoleData(EntityPlayer player, EntityNPCInterface npc) {
      if (npc.advanced.role != 0) {
         NBTTagCompound comp = new NBTTagCompound();
         npc.roleInterface.writeToNBT(comp);
         comp.setInteger("EntityId", npc.getEntityId());
         comp.setInteger("Role", npc.advanced.role);
         Server.sendData((EntityPlayerMP)player, EnumPacketClient.ROLE, comp);
      }
   }

   public static void sendFactionDataAll(EntityPlayerMP player) {
      Map<String, Integer> map = new HashMap();

      for(Faction faction : FactionController.instance.factions.values()) {
         map.put(faction.name, Integer.valueOf(faction.id));
      }

      sendScrollData(player, map);
   }

   public static void sendBankDataAll(EntityPlayerMP player) {
      Map<String, Integer> map = new HashMap();

      for(Bank bank : BankController.getInstance().banks.values()) {
         map.put(bank.name, Integer.valueOf(bank.id));
      }

      sendScrollData(player, map);
   }

   public static void openDialog(EntityPlayer player, EntityNPCInterface npc, Dialog dia) {
      Dialog dialog = dia.copy(player);
      if (!EventHooks.onNPCDialog(npc, player, dialog)) {
         if (!(npc instanceof EntityDialogNpc) && dia.id >= 0) {
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.DIALOG, npc.getEntityId(), dialog.id);
         } else {
            dialog.hideNPC = true;
            Server.sendData((EntityPlayerMP)player, EnumPacketClient.DIALOG_DUMMY, npc.getName(), dialog.writeToNBT(new NBTTagCompound()));
         }

         dia.factionOptions.addPoints(player);
         if (dialog.hasQuest()) {
            PlayerQuestController.addActiveQuest(dialog.getQuest(), player);
         }

         if (!dialog.command.isEmpty()) {
            runCommand(npc, npc.getName(), dialog.command, player);
         }

         if (dialog.mail.isValid()) {
            PlayerDataController.instance.addPlayerMessage(player.getServer(), player.getName(), dialog.mail);
         }

         PlayerData playerdata = PlayerData.get(player);
         PlayerDialogData data = playerdata.dialogData;
         if (!data.dialogsRead.contains(Integer.valueOf(dialog.id)) && dialog.id >= 0) {
            data.dialogsRead.add(Integer.valueOf(dialog.id));
            playerdata.updateClient = true;
         }

         setEditingNpc(player, npc);
      }
   }

   public static String runCommand(ICommandSender executer, String name, String command, EntityPlayer player) {
      return runCommand(executer.getEntityWorld(), executer.getPosition(), name, command, player, executer);
   }

   public static String runCommand(World world, BlockPos pos, String name, String command, EntityPlayer player, ICommandSender executer) {
      if (!world.getMinecraftServer().isCommandBlockEnabled()) {
         LogWriter.warn("Cant run commands if CommandBlocks are disabled");
         return "Cant run commands if CommandBlocks are disabled";
      } else {
         if (player != null) {
            command = command.replace("@dp", player.getName());
         }

         command = command.replace("@npc", name);
         TextComponentString output = new TextComponentString("");
         ICommandSender icommandsender = new NoppesUtilServer$1(world.getMinecraftServer(), name, output, pos, world, executer);
         ICommandManager icommandmanager = world.getMinecraftServer().getCommandManager();
         icommandmanager.executeCommand(icommandsender, command);
         return output.getUnformattedText().isEmpty() ? null : output.getUnformattedText();
      }
   }

   public static void consumeItemStack(int i, EntityPlayer player) {
      ItemStack item = player.inventory.getCurrentItem();
      if (!player.capabilities.isCreativeMode && item != null && !item.isEmpty()) {
         item.shrink(1);
         if (item.getCount() <= 0) {
            player.setHeldItem(EnumHand.MAIN_HAND, (ItemStack)null);
         }

      }
   }

   public static DataOutputStream getDataOutputStream(ByteArrayOutputStream stream) throws IOException {
      return new DataOutputStream(new GZIPOutputStream(stream));
   }

   public static void sendOpenGui(EntityPlayer player, EnumGuiType gui, EntityNPCInterface npc) {
      sendOpenGui(player, gui, npc, 0, 0, 0);
   }

   public static void sendOpenGui(EntityPlayer player, EnumGuiType gui, EntityNPCInterface npc, int i, int j, int k) {
      if (player instanceof EntityPlayerMP) {
         setEditingNpc(player, npc);
         sendExtraData(player, npc, gui, i, j, k);
         CustomNPCsScheduler.runTack(new NoppesUtilServer$2(gui, player, i, j, k, npc), 200);
      }
   }

   private static void sendExtraData(EntityPlayer player, EntityNPCInterface npc, EnumGuiType gui, int i, int j, int k) {
      if (gui == EnumGuiType.PlayerFollower || gui == EnumGuiType.PlayerFollowerHire || gui == EnumGuiType.PlayerTrader || gui == EnumGuiType.PlayerTransporter) {
         sendRoleData(player, npc);
      }

   }

   private static ArrayList<String> getScrollData(EntityPlayer player, EnumGuiType gui, EntityNPCInterface npc) {
      if (gui == EnumGuiType.PlayerTransporter) {
         RoleTransporter role = (RoleTransporter)npc.roleInterface;
         ArrayList<String> list = new ArrayList();
         TransportLocation location = role.getLocation();
         String name = role.getLocation().name;

         for(TransportLocation loc : location.category.getDefaultLocations()) {
            if (!list.contains(loc.name)) {
               list.add(loc.name);
            }
         }

         PlayerTransportData playerdata = PlayerData.get(player).transportData;
         Iterator var12 = playerdata.transports.iterator();

         while(var12.hasNext()) {
            int i = ((Integer)var12.next()).intValue();
            TransportLocation loc = TransportController.getInstance().getTransport(i);
            if (loc != null && location.category.locations.containsKey(Integer.valueOf(loc.id)) && !list.contains(loc.name)) {
               list.add(loc.name);
            }
         }

         list.remove(name);
         return list;
      } else {
         return null;
      }
   }

   public static void spawnParticle(Entity entity, String particle, int dimension) {
      Server.sendAssociatedData(entity, EnumPacketClient.PARTICLE, entity.posX, entity.posY, entity.posZ, entity.height, entity.width, particle);
   }

   public static void deleteNpc(EntityNPCInterface npc, EntityPlayer player) {
      Server.sendAssociatedData(npc, EnumPacketClient.DELETE_NPC, npc.getEntityId());
   }

   public static void createMobSpawner(BlockPos pos, NBTTagCompound comp, EntityPlayer player) {
      ServerCloneController.Instance.cleanTags(comp);
      if (comp.getString("id").equalsIgnoreCase("entityhorse")) {
         player.sendMessage(new TextComponentTranslation("Currently you cant create horse spawner, its a minecraft bug", new Object[0]));
      } else {
         player.world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState());
         TileEntityMobSpawner tile = (TileEntityMobSpawner)player.world.getTileEntity(pos);
         MobSpawnerBaseLogic logic = tile.getSpawnerBaseLogic();
         if (!comp.hasKey("id", 8)) {
            comp.setString("id", "Pig");
         }

         comp.setIntArray("StartPosNew", new int[]{pos.getX(), pos.getY(), pos.getZ()});
         logic.setNextSpawnData(new WeightedSpawnerEntity(1, comp));
      }
   }

   public static void sendQuestCategoryData(EntityPlayerMP player) {
      Map<String, Integer> map = new HashMap();

      for(QuestCategory category : QuestController.instance.categories.values()) {
         map.put(category.title, Integer.valueOf(category.id));
      }

      sendScrollData(player, map);
   }

   public static void sendPlayerData(EnumPlayerData type, EntityPlayerMP player, String name) throws IOException {
      Map<String, Integer> map = new HashMap();
      if (type == EnumPlayerData.Players) {
         for(String username : PlayerDataController.instance.nameUUIDs.keySet()) {
            map.put(username, Integer.valueOf(0));
         }
      } else {
         PlayerData playerdata = PlayerDataController.instance.getDataFromUsername(player.getServer(), name);
         if (type == EnumPlayerData.Dialog) {
            PlayerDialogData data = playerdata.dialogData;
            Iterator var6 = data.dialogsRead.iterator();

            while(var6.hasNext()) {
               int questId = ((Integer)var6.next()).intValue();
               Dialog dialog = (Dialog)DialogController.instance.dialogs.get(Integer.valueOf(questId));
               if (dialog != null) {
                  map.put(dialog.category.title + ": " + dialog.title, Integer.valueOf(questId));
               }
            }
         } else if (type == EnumPlayerData.Quest) {
            PlayerQuestData data = playerdata.questData;
            Iterator var15 = data.activeQuests.keySet().iterator();

            while(var15.hasNext()) {
               int questId = ((Integer)var15.next()).intValue();
               Quest quest = (Quest)QuestController.instance.quests.get(Integer.valueOf(questId));
               if (quest != null) {
                  map.put(quest.category.title + ": " + quest.title + "(Active quest)", Integer.valueOf(questId));
               }
            }

            var15 = data.finishedQuests.keySet().iterator();

            while(var15.hasNext()) {
               int questId = ((Integer)var15.next()).intValue();
               Quest quest = (Quest)QuestController.instance.quests.get(Integer.valueOf(questId));
               if (quest != null) {
                  map.put(quest.category.title + ": " + quest.title + "(Finished quest)", Integer.valueOf(questId));
               }
            }
         } else if (type == EnumPlayerData.Transport) {
            PlayerTransportData data = playerdata.transportData;
            Iterator var17 = data.transports.iterator();

            while(var17.hasNext()) {
               int questId = ((Integer)var17.next()).intValue();
               TransportLocation location = TransportController.getInstance().getTransport(questId);
               if (location != null) {
                  map.put(location.category.title + ": " + location.name, Integer.valueOf(questId));
               }
            }
         } else if (type == EnumPlayerData.Bank) {
            PlayerBankData data = playerdata.bankData;
            Iterator var18 = data.banks.keySet().iterator();

            while(var18.hasNext()) {
               int bankId = ((Integer)var18.next()).intValue();
               Bank bank = (Bank)BankController.getInstance().banks.get(Integer.valueOf(bankId));
               if (bank != null) {
                  map.put(bank.name, Integer.valueOf(bankId));
               }
            }
         } else if (type == EnumPlayerData.Factions) {
            PlayerFactionData data = playerdata.factionData;
            Iterator var19 = data.factionData.keySet().iterator();

            while(var19.hasNext()) {
               int factionId = ((Integer)var19.next()).intValue();
               Faction faction = (Faction)FactionController.instance.factions.get(Integer.valueOf(factionId));
               if (faction != null) {
                  map.put(faction.name + "(" + data.getFactionPoints(player, factionId) + ")", Integer.valueOf(factionId));
               }
            }
         }
      }

      sendScrollData(player, map);
   }

   public static void removePlayerData(ByteBuf buffer, EntityPlayerMP player) throws IOException {
      int id = buffer.readInt();
      if (EnumPlayerData.values().length > id) {
         String name = Server.readString(buffer);
         if (name != null && !name.isEmpty()) {
            EnumPlayerData type = EnumPlayerData.values()[id];
            EntityPlayer pl = player.getServer().getPlayerList().getPlayerByUsername(name);
            PlayerData playerdata = null;
            if (pl == null) {
               playerdata = PlayerDataController.instance.getDataFromUsername(player.getServer(), name);
            } else {
               playerdata = PlayerData.get(pl);
            }

            if (type == EnumPlayerData.Players) {
               File file = new File(CustomNpcs.getWorldSaveDirectory("playerdata"), playerdata.uuid + ".json");
               if (file.exists()) {
                  file.delete();
               }

               if (pl != null) {
                  playerdata.setNBT(new NBTTagCompound());
                  sendPlayerData(type, player, name);
                  playerdata.save(true);
                  return;
               }
            }

            if (type == EnumPlayerData.Quest) {
               PlayerQuestData data = playerdata.questData;
               int questId = buffer.readInt();
               data.activeQuests.remove(Integer.valueOf(questId));
               data.finishedQuests.remove(Integer.valueOf(questId));
               playerdata.save(true);
            }

            if (type == EnumPlayerData.Dialog) {
               PlayerDialogData data = playerdata.dialogData;
               data.dialogsRead.remove(Integer.valueOf(buffer.readInt()));
               playerdata.save(true);
            }

            if (type == EnumPlayerData.Transport) {
               PlayerTransportData data = playerdata.transportData;
               data.transports.remove(Integer.valueOf(buffer.readInt()));
               playerdata.save(true);
            }

            if (type == EnumPlayerData.Bank) {
               PlayerBankData data = playerdata.bankData;
               data.banks.remove(Integer.valueOf(buffer.readInt()));
               playerdata.save(true);
            }

            if (type == EnumPlayerData.Factions) {
               PlayerFactionData data = playerdata.factionData;
               data.factionData.remove(Integer.valueOf(buffer.readInt()));
               playerdata.save(true);
            }

            if (pl != null) {
               SyncController.syncPlayer((EntityPlayerMP)pl);
            }

            sendPlayerData(type, player, name);
         }
      }
   }

   public static void sendRecipeData(EntityPlayerMP player, int size) {
      HashMap<String, Integer> map = new HashMap();
      if (size == 3) {
         for(RecipeCarpentry recipe : RecipeController.instance.globalRecipes.values()) {
            map.put(recipe.name, Integer.valueOf(recipe.id));
         }
      } else {
         for(RecipeCarpentry recipe : RecipeController.instance.anvilRecipes.values()) {
            map.put(recipe.name, Integer.valueOf(recipe.id));
         }
      }

      sendScrollData(player, map);
   }

   public static void sendScrollData(EntityPlayerMP player, Map<String, Integer> map) {
      Map<String, Integer> send = new HashMap();

      for(String key : map.keySet()) {
         send.put(key, map.get(key));
         if (send.size() == 100) {
            Server.sendData(player, EnumPacketClient.SCROLL_DATA_PART, send);
            send = new HashMap();
         }
      }

      Server.sendData(player, EnumPacketClient.SCROLL_DATA, send);
   }

   public static void sendDialogData(EntityPlayerMP player, DialogCategory category) {
      if (category != null) {
         HashMap<String, Integer> map = new HashMap();

         for(Dialog dialog : category.dialogs.values()) {
            map.put(dialog.title, Integer.valueOf(dialog.id));
         }

         sendScrollData(player, map);
      }
   }

   public static void sendQuestData(EntityPlayerMP player, QuestCategory category) {
      if (category != null) {
         HashMap<String, Integer> map = new HashMap();

         for(Quest quest : category.quests.values()) {
            map.put(quest.title, Integer.valueOf(quest.id));
         }

         sendScrollData(player, map);
      }
   }

   public static void sendTransportCategoryData(EntityPlayerMP player) {
      HashMap<String, Integer> map = new HashMap();

      for(TransportCategory category : TransportController.getInstance().categories.values()) {
         map.put(category.title, Integer.valueOf(category.id));
      }

      sendScrollData(player, map);
   }

   public static void sendTransportData(EntityPlayerMP player, int categoryid) {
      TransportCategory category = (TransportCategory)TransportController.getInstance().categories.get(Integer.valueOf(categoryid));
      if (category != null) {
         HashMap<String, Integer> map = new HashMap();

         for(TransportLocation transport : category.locations.values()) {
            map.put(transport.name, Integer.valueOf(transport.id));
         }

         sendScrollData(player, map);
      }
   }

   public static void sendNpcDialogs(EntityPlayer player) {
      EntityNPCInterface npc = getEditingNpc(player);
      if (npc != null) {
         Iterator var2 = npc.dialogs.keySet().iterator();

         while(var2.hasNext()) {
            int pos = ((Integer)var2.next()).intValue();
            DialogOption option = (DialogOption)npc.dialogs.get(Integer.valueOf(pos));
            if (option != null && option.hasDialog()) {
               NBTTagCompound compound = option.writeNBT();
               compound.setInteger("Position", pos);
               Server.sendData((EntityPlayerMP)player, EnumPacketClient.GUI_DATA, compound);
            }
         }

      }
   }

   public static DialogOption setNpcDialog(int slot, int dialogId, EntityPlayer player) throws IOException {
      EntityNPCInterface npc = getEditingNpc(player);
      if (npc == null) {
         return null;
      } else {
         if (!npc.dialogs.containsKey(Integer.valueOf(slot))) {
            npc.dialogs.put(Integer.valueOf(slot), new DialogOption());
         }

         DialogOption option = (DialogOption)npc.dialogs.get(Integer.valueOf(slot));
         option.dialogId = dialogId;
         option.optionType = 1;
         if (option.hasDialog()) {
            option.title = option.getDialog().title;
         }

         return option;
      }
   }

   public static TileEntity saveTileEntity(EntityPlayerMP player, NBTTagCompound compound) {
      int x = compound.getInteger("x");
      int y = compound.getInteger("y");
      int z = compound.getInteger("z");
      TileEntity tile = player.world.getTileEntity(new BlockPos(x, y, z));
      if (tile != null) {
         tile.readFromNBT(compound);
      }

      return tile;
   }

   public static void setRecipeGui(EntityPlayerMP player, RecipeCarpentry recipe) {
      if (recipe != null) {
         if (player.openContainer instanceof ContainerManageRecipes) {
            ContainerManageRecipes container = (ContainerManageRecipes)player.openContainer;
            container.setRecipe(recipe);
            Server.sendData(player, EnumPacketClient.GUI_DATA, recipe.writeNBT());
         }
      }
   }

   public static void sendBank(EntityPlayerMP player, Bank bank) {
      NBTTagCompound compound = new NBTTagCompound();
      bank.writeEntityToNBT(compound);
      Server.sendData(player, EnumPacketClient.GUI_DATA, compound);
      if (player.openContainer instanceof ContainerManageBanks) {
         ((ContainerManageBanks)player.openContainer).setBank(bank);
      }

      player.sendAllContents(player.openContainer, player.openContainer.getInventory());
   }

   public static void sendNearbyNpcs(EntityPlayerMP player) {
      List<EntityNPCInterface> npcs = player.world.getEntitiesWithinAABB(EntityNPCInterface.class, player.getEntityBoundingBox().grow(120.0D, 120.0D, 120.0D));
      HashMap<String, Integer> map = new HashMap();

      for(EntityNPCInterface npc : npcs) {
         if (!npc.isDead) {
            float distance = player.getDistance(npc);
            DecimalFormat df = new DecimalFormat("#.#");
            String s = df.format((double)distance);
            if (distance < 10.0F) {
               s = "0" + s;
            }

            map.put(s + " : " + npc.display.getName(), Integer.valueOf(npc.getEntityId()));
         }
      }

      sendScrollData(player, map);
   }

   public static void sendGuiError(EntityPlayer player, int i) {
      Server.sendData((EntityPlayerMP)player, EnumPacketClient.GUI_ERROR, i, new NBTTagCompound());
   }

   public static void sendGuiClose(EntityPlayerMP player, int i, NBTTagCompound comp) {
      Server.sendData(player, EnumPacketClient.GUI_CLOSE, i, comp);
   }

   public static Entity spawnClone(NBTTagCompound compound, double x, double y, double z, World world) {
      ServerCloneController.Instance.cleanTags(compound);
      compound.setTag("Pos", NBTTags.nbtDoubleList(x, y, z));
      Entity entity = EntityList.createEntityFromNBT(compound, world);
      if (entity == null) {
         return null;
      } else {
         if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            npc.ais.setStartPos(new BlockPos(npc));
         }

         world.spawnEntity(entity);
         return entity;
      }
   }

   public static boolean isOp(EntityPlayer player) {
      return player.getServer().getPlayerList().canSendCommands(player.getGameProfile());
   }

   public static void GivePlayerItem(Entity entity, EntityPlayer player, ItemStack item) {
      if (!entity.world.isRemote && item != null && !item.isEmpty()) {
         item = item.copy();
         float f = 0.7F;
         double d = (double)(entity.world.rand.nextFloat() * f) + (double)(1.0F - f);
         double d1 = (double)(entity.world.rand.nextFloat() * f) + (double)(1.0F - f);
         double d2 = (double)(entity.world.rand.nextFloat() * f) + (double)(1.0F - f);
         EntityItem entityitem = new EntityItem(entity.world, entity.posX + d, entity.posY + d1, entity.posZ + d2, item);
         entityitem.setPickupDelay(2);
         entity.world.spawnEntity(entityitem);
         int i = item.getCount();
         if (player.inventory.addItemStackToInventory(item)) {
            entity.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.onItemPickup(entityitem, i);
            PlayerQuestData playerdata = PlayerData.get(player).questData;
            playerdata.checkQuestCompletion(player, 0);
            if (item.getCount() <= 0) {
               entityitem.setDead();
            }
         }

      }
   }

   public static BlockPos GetClosePos(BlockPos origin, World world) {
      for(int x = -1; x < 2; ++x) {
         for(int z = -1; z < 2; ++z) {
            for(int y = 2; y >= -2; --y) {
               BlockPos pos = origin.add(x, y, z);
               if (world.isSideSolid(pos, EnumFacing.UP) && world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2))) {
                  return pos.up();
               }
            }
         }
      }

      return world.getTopSolidOrLiquidBlock(origin);
   }

   public static void NotifyOPs(String message, Object... obs) {
      TextComponentTranslation chatcomponenttranslation = new TextComponentTranslation(message, obs);
      chatcomponenttranslation.getStyle().setColor(TextFormatting.GRAY);
      chatcomponenttranslation.getStyle().setItalic(Boolean.valueOf(true));

      for(EntityPlayer entityplayer : CustomNpcs.Server.getPlayerList().getPlayers()) {
         if (entityplayer.sendCommandFeedback() && isOp(entityplayer)) {
            entityplayer.sendMessage(chatcomponenttranslation);
         }
      }

      if (CustomNpcs.Server.worlds[0].getGameRules().getBoolean("logAdminCommands")) {
         LogWriter.info(chatcomponenttranslation.getUnformattedText());
      }

   }

   public static void playSound(EntityLivingBase entity, SoundEvent sound, float volume, float pitch) {
      entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, sound, SoundCategory.NEUTRAL, volume, pitch);
   }

   public static void playSound(World world, BlockPos pos, SoundEvent sound, SoundCategory cat, float volume, float pitch) {
      world.playSound((EntityPlayer)null, pos, sound, cat, volume, pitch);
   }

   public static EntityPlayer getPlayer(MinecraftServer minecraftserver, UUID id) {
      for(EntityPlayer player : minecraftserver.getPlayerList().getPlayers()) {
         if (id.equals(player.getUniqueID())) {
            return player;
         }
      }

      return null;
   }

   public static Entity GetDamageSourcee(DamageSource damagesource) {
      Entity entity = damagesource.getTrueSource();
      if (entity == null) {
         entity = damagesource.getImmediateSource();
      }

      if (entity instanceof EntityArrow && ((EntityArrow)entity).shootingEntity instanceof EntityLivingBase) {
         entity = (EntityLivingBase)((EntityArrow)entity).shootingEntity;
      } else if (entity instanceof EntityThrowable) {
         entity = ((EntityThrowable)entity).getThrower();
      }

      return entity;
   }

   public static boolean IsItemStackNull(ItemStack is) {
      return is == null || is.isEmpty() || is == ItemStack.EMPTY || is.getItem() == null;
   }

   public static ItemStack ChangeItemStack(ItemStack is, Item item) {
      NBTTagCompound comp = is.writeToNBT(new NBTTagCompound());
      ResourceLocation resourcelocation = (ResourceLocation)Item.REGISTRY.getNameForObject(item);
      comp.setString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
      return new ItemStack(comp);
   }

   // $FF: synthetic method
   static ArrayList access$000(EntityPlayer x0, EnumGuiType x1, EntityNPCInterface x2) {
      return getScrollData(x0, x1, x2);
   }
}
