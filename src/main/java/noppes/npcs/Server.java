package noppes.npcs;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.util.CustomNPCsScheduler;

public class Server {
   public static void sendData(EntityPlayerMP player, EnumPacketClient enu, Object... obs) {
      sendDataDelayed(player, enu, 0, obs);
   }

   public static void sendDataDelayed(EntityPlayerMP player, EnumPacketClient enu, int delay, Object... obs) {
      CustomNPCsScheduler.runTack(new Server$1(enu, obs, player), delay);
   }

   public static boolean sendDataChecked(EntityPlayerMP player, EnumPacketClient enu, Object... obs) {
      PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

      try {
         if (!fillBuffer(buffer, enu, obs)) {
            return false;
         }

         CustomNpcs.Channel.sendTo(new FMLProxyPacket(buffer, "CustomNPCs"), player);
      } catch (IOException var5) {
         LogWriter.error(enu + " Errored", var5);
      }

      return true;
   }

   public static void sendAssociatedData(Entity entity, EnumPacketClient enu, Object... obs) {
      List<EntityPlayerMP> list = entity.world.getEntitiesWithinAABB(EntityPlayerMP.class, entity.getEntityBoundingBox().grow(160.0D, 160.0D, 160.0D));
      if (!list.isEmpty()) {
         CustomNPCsScheduler.runTack(new Server$2(enu, obs, list));
      }
   }

   public static void sendToAll(MinecraftServer server, EnumPacketClient enu, Object... obs) {
      List<EntityPlayerMP> list = new ArrayList(server.getPlayerList().getPlayers());
      CustomNPCsScheduler.runTack(new Server$3(enu, obs, list));
   }

   public static boolean fillBuffer(ByteBuf buffer, Enum enu, Object... obs) throws IOException {
      buffer.writeInt(enu.ordinal());

      for(Object ob : obs) {
         if (ob != null) {
            if (ob instanceof Map) {
               Map<String, Integer> map = (Map)ob;
               buffer.writeInt(map.size());

               for(String key : map.keySet()) {
                  int value = ((Integer)map.get(key)).intValue();
                  buffer.writeInt(value);
                  writeString(buffer, key);
               }
            } else if (ob instanceof MerchantRecipeList) {
               ((MerchantRecipeList)ob).writeToBuf(new PacketBuffer(buffer));
            } else if (ob instanceof List) {
               List<String> list = (List)ob;
               buffer.writeInt(list.size());

               for(String s : list) {
                  writeString(buffer, s);
               }
            } else if (ob instanceof UUID) {
               writeString(buffer, ob.toString());
            } else if (ob instanceof Enum) {
               buffer.writeInt(((Enum)ob).ordinal());
            } else if (ob instanceof Integer) {
               buffer.writeInt(((Integer)ob).intValue());
            } else if (ob instanceof Boolean) {
               buffer.writeBoolean(((Boolean)ob).booleanValue());
            } else if (ob instanceof String) {
               writeString(buffer, (String)ob);
            } else if (ob instanceof Float) {
               buffer.writeFloat(((Float)ob).floatValue());
            } else if (ob instanceof Long) {
               buffer.writeLong(((Long)ob).longValue());
            } else if (ob instanceof Double) {
               buffer.writeDouble(((Double)ob).doubleValue());
            } else if (ob instanceof NBTTagCompound) {
               writeNBT(buffer, (NBTTagCompound)ob);
            }
         }
      }

      if (buffer.array().length >= 32767) {
         LogWriter.error("Packet " + enu + " was too big to be send");
         return false;
      } else {
         return true;
      }
   }

   public static UUID readUUID(ByteBuf buffer) {
      return UUID.fromString(readString(buffer));
   }

   public static void writeNBT(ByteBuf buffer, NBTTagCompound compound) throws IOException {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));

      try {
         CompressedStreamTools.write(compound, dataoutputstream);
      } finally {
         dataoutputstream.close();
      }

      byte[] bytes = bytearrayoutputstream.toByteArray();
      buffer.writeShort((short)bytes.length);
      buffer.writeBytes(bytes);
   }

   public static NBTTagCompound readNBT(ByteBuf buffer) throws IOException {
      byte[] bytes = new byte[buffer.readShort()];
      buffer.readBytes(bytes);
      DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));

      NBTTagCompound var3;
      try {
         var3 = CompressedStreamTools.read(datainputstream, NBTSizeTracker.INFINITE);
      } finally {
         datainputstream.close();
      }

      return var3;
   }

   public static void writeString(ByteBuf buffer, String s) {
      byte[] bytes = s.getBytes(Charsets.UTF_8);
      buffer.writeShort((short)bytes.length);
      buffer.writeBytes(bytes);
   }

   public static String readString(ByteBuf buffer) {
      try {
         byte[] bytes = new byte[buffer.readShort()];
         buffer.readBytes(bytes);
         return new String(bytes, Charsets.UTF_8);
      } catch (IndexOutOfBoundsException var2) {
         return null;
      }
   }
}
