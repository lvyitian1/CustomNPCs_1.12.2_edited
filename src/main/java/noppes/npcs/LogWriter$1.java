package noppes.npcs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

final class LogWriter$1 extends Formatter {
   public String format(LogRecord record) {
      StackTraceElement element = Thread.currentThread().getStackTrace()[8];
      String line = "[" + element.getClassName() + ":" + element.getLineNumber() + "] ";
      String time = "[" + LogWriter.access$000().format(new Date(record.getMillis())) + "][" + record.getLevel() + "/" + "CustomNPCs" + "]" + line;
      if (record.getThrown() != null) {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         record.getThrown().printStackTrace(pw);
         return time + sw.toString();
      } else {
         return time + record.getMessage() + System.getProperty("line.separator");
      }
   }
}
