package codeu.chat.util.logging;

import java.util.Date;
import java.util.logging.Level;

/**
 * A logging channel used to write log information to standard out (the console window).
 */
final class ConsoleChannel implements Channel {

  @Override
  public void write(Level level, Date time, StackTraceElement[] stack, String message) {
    System.out.format("[%s] %s %s %s : %s\n",
        level,
        time,
        stack[2].getClassName(),
        stack[2].getMethodName(),
        message);
  }
}
