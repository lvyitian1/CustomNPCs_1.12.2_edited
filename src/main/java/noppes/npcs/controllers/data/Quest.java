package noppes.npcs.controllers.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.ICompatibilty;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.Server;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.constants.EnumQuestRepeat;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.quests.QuestDialog;
import noppes.npcs.quests.QuestInterface;
import noppes.npcs.quests.QuestItem;
import noppes.npcs.quests.QuestKill;
import noppes.npcs.quests.QuestLocation;

public class Quest implements ICompatibilty, IQuest {
   public int version = VersionCompatibility.ModRev;
   public int id = -1;
   public int type = 0;
   public EnumQuestRepeat repeat = EnumQuestRepeat.NONE;
   public EnumQuestCompletion completion = EnumQuestCompletion.Npc;
   public String title = "default";
   public final QuestCategory category;
   public String logText = "";
   public String completeText = "";
   public String completerNpc = "";
   public int nextQuestid = -1;
   public String nextQuestTitle = "";
   public PlayerMail mail = new PlayerMail();
   public String command = "";
   public QuestInterface questInterface = new QuestItem();
   public int rewardExp = 0;
   public NpcMiscInventory rewardItems = new NpcMiscInventory(9);
   public boolean randomReward = false;
   public FactionOptions factionOptions = new FactionOptions();

   public Quest(QuestCategory category) {
      this.category = category;
   }

   public void readNBT(NBTTagCompound compound) {
      this.id = compound.getInteger("Id");
      this.readNBTPartial(compound);
   }

   public void readNBTPartial(NBTTagCompound compound) {
      this.version = compound.getInteger("ModRev");
      VersionCompatibility.CheckAvailabilityCompatibility(this, compound);
      this.setType(compound.getInteger("Type"));
      this.title = compound.getString("Title");
      this.logText = compound.getString("Text");
      this.completeText = compound.getString("CompleteText");
      this.completerNpc = compound.getString("CompleterNpc");
      this.command = compound.getString("QuestCommand");
      this.nextQuestid = compound.getInteger("NextQuestId");
      this.nextQuestTitle = compound.getString("NextQuestTitle");
      if (this.hasNewQuest()) {
         this.nextQuestTitle = this.getNextQuest().title;
      } else {
         this.nextQuestTitle = "";
      }

      this.randomReward = compound.getBoolean("RandomReward");
      this.rewardExp = compound.getInteger("RewardExp");
      this.rewardItems.setFromNBT(compound.getCompoundTag("Rewards"));
      this.completion = EnumQuestCompletion.values()[compound.getInteger("QuestCompletion")];
      this.repeat = EnumQuestRepeat.values()[compound.getInteger("QuestRepeat")];
      this.questInterface.readEntityFromNBT(compound);
      this.factionOptions.readFromNBT(compound.getCompoundTag("QuestFactionPoints"));
      this.mail.readNBT(compound.getCompoundTag("QuestMail"));
   }

   public void setType(int questType) {
      this.type = questType;
      if (this.type == 0) {
         this.questInterface = new QuestItem();
      } else if (this.type == 1) {
         this.questInterface = new QuestDialog();
      } else if (this.type != 2 && this.type != 4) {
         if (this.type == 3) {
            this.questInterface = new QuestLocation();
         }
      } else {
         this.questInterface = new QuestKill();
      }

      if (this.questInterface != null) {
         this.questInterface.questId = this.id;
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setInteger("Id", this.id);
      return this.writeToNBTPartial(compound);
   }

   public NBTTagCompound writeToNBTPartial(NBTTagCompound compound) {
      compound.setInteger("ModRev", this.version);
      compound.setInteger("Type", this.type);
      compound.setString("Title", this.title);
      compound.setString("Text", this.logText);
      compound.setString("CompleteText", this.completeText);
      compound.setString("CompleterNpc", this.completerNpc);
      compound.setInteger("NextQuestId", this.nextQuestid);
      compound.setString("NextQuestTitle", this.nextQuestTitle);
      compound.setInteger("RewardExp", this.rewardExp);
      compound.setTag("Rewards", this.rewardItems.getToNBT());
      compound.setString("QuestCommand", this.command);
      compound.setBoolean("RandomReward", this.randomReward);
      compound.setInteger("QuestCompletion", this.completion.ordinal());
      compound.setInteger("QuestRepeat", this.repeat.ordinal());
      this.questInterface.writeEntityToNBT(compound);
      compound.setTag("QuestFactionPoints", this.factionOptions.writeToNBT(new NBTTagCompound()));
      compound.setTag("QuestMail", this.mail.writeNBT());
      return compound;
   }

   public boolean hasNewQuest() {
      return this.getNextQuest() != null;
   }

   public Quest getNextQuest() {
      return QuestController.instance == null ? null : (Quest)QuestController.instance.quests.get(Integer.valueOf(this.nextQuestid));
   }

   public boolean complete(EntityPlayer player, QuestData data) {
      if (this.completion == EnumQuestCompletion.Instant) {
         Server.sendData((EntityPlayerMP)player, EnumPacketClient.QUEST_COMPLETION, data.quest.id);
         return true;
      } else {
         return false;
      }
   }

   public Quest copy() {
      Quest quest = new Quest(this.category);
      quest.readNBT(this.writeToNBT(new NBTTagCompound()));
      return quest;
   }

   public int getVersion() {
      return this.version;
   }

   public void setVersion(int version) {
      this.version = version;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.title;
   }

   public int getType() {
      return this.type;
   }

   public IQuestCategory getCategory() {
      return this.category;
   }

   public void save() {
      QuestController.instance.saveQuest(this.category, this);
   }

   public void setName(String name) {
      this.title = name;
   }

   public String getLogText() {
      return this.logText;
   }

   public void setLogText(String text) {
      this.logText = text;
   }

   public String getCompleteText() {
      return this.completeText;
   }

   public void setCompleteText(String text) {
      this.completeText = text;
   }

   public void setNextQuest(IQuest quest) {
      if (quest == null) {
         this.nextQuestid = -1;
         this.nextQuestTitle = "";
      } else {
         if (quest.getId() < 0) {
            throw new CustomNPCsException("Quest id is lower than 0", new Object[0]);
         }

         this.nextQuestid = quest.getId();
         this.nextQuestTitle = quest.getName();
      }

   }

   public String getNpcName() {
      return this.completerNpc;
   }

   public void setNpcName(String name) {
      this.completerNpc = name;
   }

   public String[] getObjective(IPlayer player) {
      return this.questInterface.getQuestLogStatus(player.getMCEntity());
   }


}
