package noppes.npcs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class LogWriter {
   private static final String name = "CustomNPCs";
   private static final Logger logger = Logger.getLogger("CustomNPCs");
   private static final SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
   private static Handler handler;

   public static void info(Object msg) {
      logger.log(Level.FINE, msg.toString());
      handler.flush();
   }

   public static void warn(Object msg) {
      logger.log(Level.WARNING, msg.toString());
      handler.flush();
   }

   public static void error(Object msg) {
      logger.log(Level.SEVERE, msg.toString());
      handler.flush();
   }

   public static void error(Object msg, Exception e) {
      logger.log(Level.SEVERE, msg.toString());
      logger.log(Level.SEVERE, e.getMessage(), e);
      handler.flush();
   }

   public static void except(Exception e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
      handler.flush();
   }

   // $FF: synthetic method
   static SimpleDateFormat access$000() {
      return dateformat;
   }

   static {
      try {
         File dir = new File("logs");
         if (!dir.exists()) {
            dir.mkdir();
         }

         File file = new File(dir, "CustomNPCs-latest.log");
         File lock = new File(dir, "CustomNPCs-latest.log.lck");
         File file1 = new File(dir, "CustomNPCs-1.log");
         File file2 = new File(dir, "CustomNPCs-2.log");
         File file3 = new File(dir, "CustomNPCs-3.log");
         if (lock.exists()) {
            lock.delete();
         }

         if (file3.exists()) {
            file3.delete();
         }

         if (file2.exists()) {
            file2.renameTo(file3);
         }

         if (file1.exists()) {
            file1.renameTo(file2);
         }

         if (file.exists()) {
            file.renameTo(file1);
         }

         handler = new StreamHandler(new FileOutputStream(file), new LogWriter$1());
         handler.setLevel(Level.ALL);
         logger.addHandler(handler);
         logger.setUseParentHandlers(false);
         Handler consoleHandler = new ConsoleHandler();
         consoleHandler.setFormatter(handler.getFormatter());
         consoleHandler.setLevel(Level.ALL);
         logger.addHandler(consoleHandler);
         logger.setLevel(Level.ALL);
         info((new Date()).toString());
      } catch (SecurityException var7) {
         var7.printStackTrace();
      } catch (IOException var8) {
         var8.printStackTrace();
      }

   }
}
