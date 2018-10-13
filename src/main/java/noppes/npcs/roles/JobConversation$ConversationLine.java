package noppes.npcs.roles;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.Line;

public class JobConversation$ConversationLine extends Line {
   public String npc;
   public int delay;
   // $FF: synthetic field
   final JobConversation this$0;

   public JobConversation$ConversationLine(JobConversation this$0) {
      this.this$0 = this$0;
      this.npc = "";
      this.delay = 40;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setString("Line", this.text);
      compound.setString("Npc", this.npc);
      compound.setString("Sound", this.sound);
      compound.setInteger("Delay", this.delay);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.text = compound.getString("Line");
      this.npc = compound.getString("Npc");
      this.sound = compound.getString("Sound");
      this.delay = compound.getInteger("Delay");
   }

   public boolean isEmpty() {
      return this.npc.isEmpty() || this.text.isEmpty();
   }
}
