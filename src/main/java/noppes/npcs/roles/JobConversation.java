package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;

public class JobConversation extends JobInterface {
   public Availability availability = new Availability();
   private ArrayList<String> names = new ArrayList();
   private HashMap<String, EntityNPCInterface> npcs = new HashMap();
   public HashMap<Integer, JobConversation$ConversationLine> lines = new HashMap();
   public int quest = -1;
   public String questTitle = "";
   public int generalDelay = 400;
   public int ticks = 100;
   public int range = 20;
   private JobConversation$ConversationLine nextLine;
   private boolean hasStarted = false;
   private int startedTicks = 20;
   public int mode = 0;

   public JobConversation(EntityNPCInterface npc) {
      super(npc);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setTag("ConversationAvailability", this.availability.writeToNBT(new NBTTagCompound()));
      compound.setInteger("ConversationQuest", this.quest);
      compound.setInteger("ConversationDelay", this.generalDelay);
      compound.setInteger("ConversationRange", this.range);
      compound.setInteger("ConversationMode", this.mode);
      NBTTagList nbttaglist = new NBTTagList();
      Iterator var3 = this.lines.keySet().iterator();

      while(var3.hasNext()) {
         int slot = ((Integer)var3.next()).intValue();
         JobConversation$ConversationLine line = (JobConversation$ConversationLine)this.lines.get(Integer.valueOf(slot));
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setInteger("Slot", slot);
         line.writeEntityToNBT(nbttagcompound);
         nbttaglist.appendTag(nbttagcompound);
      }

      compound.setTag("ConversationLines", nbttaglist);
      if (this.hasQuest()) {
         compound.setString("ConversationQuestTitle", this.getQuest().title);
      }

      return compound;
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.names.clear();
      this.availability.readFromNBT(compound.getCompoundTag("ConversationAvailability"));
      this.quest = compound.getInteger("ConversationQuest");
      this.generalDelay = compound.getInteger("ConversationDelay");
      this.questTitle = compound.getString("ConversationQuestTitle");
      this.range = compound.getInteger("ConversationRange");
      this.mode = compound.getInteger("ConversationMode");
      NBTTagList nbttaglist = compound.getTagList("ConversationLines", 10);
      HashMap<Integer, JobConversation$ConversationLine> map = new HashMap();

      for(int i = 0; i < nbttaglist.tagCount(); ++i) {
         NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
         JobConversation$ConversationLine line = new JobConversation$ConversationLine(this);
         line.readEntityFromNBT(nbttagcompound);
         if (!line.npc.isEmpty() && !this.names.contains(line.npc.toLowerCase())) {
            this.names.add(line.npc.toLowerCase());
         }

         map.put(Integer.valueOf(nbttagcompound.getInteger("Slot")), line);
      }

      this.lines = map;
      this.ticks = this.generalDelay;
   }

   public boolean hasQuest() {
      return this.getQuest() != null;
   }

   public Quest getQuest() {
      return this.npc.isRemote() ? null : (Quest)QuestController.instance.quests.get(Integer.valueOf(this.quest));
   }

   public void aiUpdateTask() {
      --this.ticks;
      if (this.ticks <= 0 && this.nextLine != null) {
         this.say(this.nextLine);
         boolean seenNext = false;
         JobConversation$ConversationLine compare = this.nextLine;
         this.nextLine = null;

         for(JobConversation$ConversationLine line : this.lines.values()) {
            if (!line.isEmpty()) {
               if (seenNext) {
                  this.nextLine = line;
                  break;
               }

               if (line == compare) {
                  seenNext = true;
               }
            }
         }

         if (this.nextLine != null) {
            this.ticks = this.nextLine.delay;
         } else if (this.hasQuest()) {
            for(EntityPlayer player : this.npc.world.getEntitiesWithinAABB(EntityPlayer.class, this.npc.getEntityBoundingBox().grow((double)this.range, (double)this.range, (double)this.range))) {
               if (this.availability.isAvailable(player)) {
                  PlayerQuestController.addActiveQuest(this.getQuest(), player);
               }
            }
         }

      }
   }

   public boolean aiShouldExecute() {
      if (!this.lines.isEmpty() && !this.npc.isKilled() && !this.npc.isAttacking() && this.shouldRun()) {
         if (!this.hasStarted && this.mode == 1) {
            if (this.startedTicks-- > 0) {
               return false;
            }

            this.startedTicks = 10;
            if (this.npc.world.getEntitiesWithinAABB(EntityPlayer.class, this.npc.getEntityBoundingBox().grow((double)this.range, (double)this.range, (double)this.range)).isEmpty()) {
               return false;
            }
         }

         for(JobConversation$ConversationLine line : this.lines.values()) {
            if (line != null && !line.isEmpty()) {
               this.nextLine = line;
               break;
            }
         }

         return this.nextLine != null;
      } else {
         return false;
      }
   }

   private boolean shouldRun() {
      --this.ticks;
      if (this.ticks > 0) {
         return false;
      } else {
         this.npcs.clear();

         for(EntityNPCInterface npc : this.npc.world.getEntitiesWithinAABB(EntityNPCInterface.class, this.npc.getEntityBoundingBox().grow(10.0D, 10.0D, 10.0D))) {
            if (!npc.isKilled() && !npc.isAttacking() && this.names.contains(npc.getName().toLowerCase())) {
               this.npcs.put(npc.getName().toLowerCase(), npc);
            }
         }

         boolean bo = this.names.size() == this.npcs.size();
         if (!bo) {
            this.ticks = 20;
         }

         return bo;
      }
   }

   public boolean aiContinueExecute() {
      for(EntityNPCInterface npc : this.npcs.values()) {
         if (npc.isKilled() || npc.isAttacking()) {
            return false;
         }
      }

      return this.nextLine != null;
   }

   public void resetTask() {
      this.nextLine = null;
      this.ticks = this.generalDelay;
      this.hasStarted = false;
   }

   public void aiStartExecuting() {
      this.startedTicks = 20;
      this.hasStarted = true;
   }

   private void say(JobConversation$ConversationLine line) {
      List<EntityPlayer> inRange = this.npc.world.getEntitiesWithinAABB(EntityPlayer.class, this.npc.getEntityBoundingBox().grow((double)this.range, (double)this.range, (double)this.range));
      EntityNPCInterface npc = (EntityNPCInterface)this.npcs.get(line.npc.toLowerCase());
      if (npc != null) {
         for(EntityPlayer player : inRange) {
            if (this.availability.isAvailable(player)) {
               npc.say(player, line);
            }
         }

      }
   }

   public void reset() {
      this.hasStarted = false;
      this.resetTask();
      this.ticks = 60;
   }

   public void killed() {
      this.reset();
   }

   public JobConversation$ConversationLine getLine(int slot) {
      if (this.lines.containsKey(Integer.valueOf(slot))) {
         return (JobConversation$ConversationLine)this.lines.get(Integer.valueOf(slot));
      } else {
         JobConversation$ConversationLine line = new JobConversation$ConversationLine(this);
         this.lines.put(Integer.valueOf(slot), line);
         return line;
      }
   }
}
