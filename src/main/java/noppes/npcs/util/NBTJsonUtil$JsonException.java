package noppes.npcs.util;

public class NBTJsonUtil$JsonException extends Exception {
   public NBTJsonUtil$JsonException(String message, NBTJsonUtil$JsonFile json) {
      super(message + ": " + json.getCurrentPos());
   }
}
