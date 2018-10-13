package noppes.npcs.api.wrapper;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPixelmon;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.util.ValueUtil;

public class PixelmonWrapper<T extends EntityTameable> extends AnimalWrapper<T> implements IPixelmon {
   public PixelmonWrapper(T pixelmon) {
      super(pixelmon);
   }

   private NBTTagCompound getCompound() {
      NBTTagCompound compound = new NBTTagCompound();
      ((EntityTameable)this.entity).writeEntityToNBT(compound);
      return compound;
   }

   public boolean getIsShiny() {
      return this.getCompound().getBoolean("IsShiny");
   }

   public void setIsShiny(boolean bo) {
      NBTTagCompound compound = this.getCompound();
      compound.setBoolean("IsShiny", bo);
      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public int getLevel() {
      return this.getCompound().getInteger("Level");
   }

   public void setLevel(int level) {
      NBTTagCompound compound = this.getCompound();
      compound.setInteger("Level", level);
      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public int getIV(int type) {
      NBTTagCompound compound = this.getCompound();
      if (type == 0) {
         return compound.getInteger("IVHP");
      } else if (type == 1) {
         return compound.getInteger("IVAttack");
      } else if (type == 2) {
         return compound.getInteger("IVDefence");
      } else if (type == 3) {
         return compound.getInteger("IVSpAtt");
      } else if (type == 4) {
         return compound.getInteger("IVSpDef");
      } else {
         return type == 5 ? compound.getInteger("IVSpeed") : -1;
      }
   }

   public void setIV(int type, int value) {
      NBTTagCompound compound = this.getCompound();
      if (type == 0) {
         compound.setInteger("IVHP", value);
      } else if (type == 1) {
         compound.setInteger("IVAttack", value);
      } else if (type == 2) {
         compound.setInteger("IVDefence", value);
      } else if (type == 3) {
         compound.setInteger("IVSpAtt", value);
      } else if (type == 4) {
         compound.setInteger("IVSpDef", value);
      } else if (type == 5) {
         compound.setInteger("IVSpeed", value);
      }

      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public int getEV(int type) {
      NBTTagCompound compound = this.getCompound();
      if (type == 0) {
         return compound.getInteger("EVHP");
      } else if (type == 1) {
         return compound.getInteger("EVAttack");
      } else if (type == 2) {
         return compound.getInteger("EVDefence");
      } else if (type == 3) {
         return compound.getInteger("EVSpecialAttack");
      } else if (type == 4) {
         return compound.getInteger("EVSpecialDefence");
      } else {
         return type == 5 ? compound.getInteger("EVSpeed") : -1;
      }
   }

   public void setEV(int type, int value) {
      NBTTagCompound compound = this.getCompound();
      if (type == 0) {
         compound.setInteger("EVHP", value);
      } else if (type == 1) {
         compound.setInteger("EVAttack", value);
      } else if (type == 2) {
         compound.setInteger("EVDefence", value);
      } else if (type == 3) {
         compound.setInteger("EVSpecialAttack", value);
      } else if (type == 4) {
         compound.setInteger("EVSpecialDefence", value);
      } else if (type == 5) {
         compound.setInteger("EVSpeed", value);
      }

      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public int getStat(int type) {
      NBTTagCompound compound = this.getCompound();
      if (type == 0) {
         return compound.getInteger("StatsHP");
      } else if (type == 1) {
         return compound.getInteger("StatsAttack");
      } else if (type == 2) {
         return compound.getInteger("StatsDefence");
      } else if (type == 3) {
         return compound.getInteger("StatsSpecialAttack");
      } else if (type == 4) {
         return compound.getInteger("StatsSpecialDefence");
      } else {
         return type == 5 ? compound.getInteger("StatsSpeed") : -1;
      }
   }

   public void setStat(int type, int value) {
      NBTTagCompound compound = this.getCompound();
      if (type == 0) {
         compound.setInteger("StatsHP", value);
      } else if (type == 1) {
         compound.setInteger("StatsAttack", value);
      } else if (type == 2) {
         compound.setInteger("StatsDefence", value);
      } else if (type == 3) {
         compound.setInteger("StatsSpecialAttack", value);
      } else if (type == 4) {
         compound.setInteger("StatsSpecialDefence", value);
      } else if (type == 5) {
         compound.setInteger("StatsSpeed", value);
      }

      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public int getSize() {
      NBTTagCompound compound = this.getCompound();
      return compound.getShort("Growth");
   }

   public void setSize(int type) {
      NBTTagCompound compound = this.getCompound();
      compound.setShort("Growth", (short)type);
      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public int getHapiness() {
      NBTTagCompound compound = this.getCompound();
      return compound.getInteger("Friendship");
   }

   public void setHapiness(int value) {
      NBTTagCompound compound = this.getCompound();
      value = ValueUtil.CorrectInt(value, 0, 255);
      compound.setInteger("Friendship", value);
      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public int getNature() {
      NBTTagCompound compound = this.getCompound();
      return compound.getShort("Nature");
   }

   public void setNature(int type) {
      NBTTagCompound compound = this.getCompound();
      compound.setShort("Nature", (short)type);
      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public int getPokeball() {
      NBTTagCompound compound = this.getCompound();
      return compound.hasKey("CaughtBall") ? -1 : compound.getInteger("CaughtBall");
   }

   public void setPokeball(int type) {
      NBTTagCompound compound = this.getCompound();
      compound.setInteger("CaughtBall", type);
      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public String getNickname() {
      NBTTagCompound compound = this.getCompound();
      return compound.getString("Nickname");
   }

   public boolean hasNickname() {
      return !this.getNickname().isEmpty();
   }

   public void setNickname(String name) {
      NBTTagCompound compound = this.getCompound();
      compound.setString("Nickname", name);
      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }

   public String getMove(int slot) {
      NBTTagCompound compound = this.getCompound();
      return !compound.hasKey("PixelmonMoveID" + slot) ? null : PixelmonHelper.getAttackName(compound.getInteger("PixelmonMoveID" + slot));
   }

   public int getType() {
      return 8;
   }

   public void setMove(int slot, String move) {
      NBTTagCompound compound = this.getCompound();
      slot = ValueUtil.CorrectInt(slot, 0, 3);
      int id = PixelmonHelper.getAttackID(move);
      compound.removeTag("PixelmonMovePP" + slot);
      compound.removeTag("PixelmonMovePPBase" + slot);
      if (id < 0) {
         compound.removeTag("PixelmonMoveID" + slot);
      } else {
         compound.setInteger("PixelmonMoveID" + slot, id);
      }

      int size = 0;

      for(int i = 0; i < 4; ++i) {
         if (compound.hasKey("PixelmonMoveID" + i)) {
            ++size;
         }
      }

      compound.setInteger("PixelmonNumberMoves", size);
      ((EntityTameable)this.entity).readEntityFromNBT(compound);
   }
}
