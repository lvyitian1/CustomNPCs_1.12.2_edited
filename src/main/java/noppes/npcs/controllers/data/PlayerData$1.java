package noppes.npcs.controllers.data;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

class PlayerData$1 implements Runnable {
   // $FF: synthetic field
   final String val$filename;
   // $FF: synthetic field
   final NBTTagCompound val$compound;
   // $FF: synthetic field
   final PlayerData this$0;

   PlayerData$1(PlayerData this$0, String var2, NBTTagCompound var3) {
      this.this$0 = this$0;
      this.val$filename = var2;
      this.val$compound = var3;
   }

   public void run() {
      try {
         File saveDir = CustomNpcs.getWorldSaveDirectory("playerdata");
         File file = new File(saveDir, this.val$filename + "_new");
         File file1 = new File(saveDir, this.val$filename);
         NBTJsonUtil.SaveFile(file, this.val$compound);
         if (file1.exists()) {
            file1.delete();
         }

         file.renameTo(file1);
      } catch (Exception var4) {
         LogWriter.except(var4);
      }

   }
}
