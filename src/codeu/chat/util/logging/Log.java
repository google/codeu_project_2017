package codeu.chat.util.logging;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;

public final class Log {

  // The single instance of the log, anytime someone wants access to the log they will use
  // |Log.instance|.
  public static final Log instance = new Log();

  // An internal lock to ensure that messages are only written one at a time. This is to avoid
  // multiple threads from writing to the log at the same time.
  private final Object mLock = new Object();

  private final HashSet<Channel> mInfo = new HashSet<>();
  private final HashSet<Channel> mWarning = new HashSet<>();
  private final HashSet<Channel> mError = new HashSet<>();

  private Log() {
    var defaultChannel = new ConsoleChannel();

    // Initialize the system to have a single channel. If additional channels are wanted (e.g. a
    // channel to write data to a file) they can be added here.
    mInfo.add(defaultChannel);
    mWarning.add(defaultChannel);
    mError.add(defaultChannel);
  }

  public void info(String message, Object... params) {
    var fullMessage = String.format(message, params);
    write(mInfo, Level.INFO, new Date(), Thread.currentThread().getStackTrace(), fullMessage);
  }

  public void warning(String message, Object... params) {
    var fullMessage = String.format(message, params);
    write(mWarning, Level.WARNING, new Date(), Thread.currentThread().getStackTrace(), fullMessage);
  }

  public void error(String message, Object... params) {
    var fullMessage = String.format(message, params);
    write(mError, Level.SEVERE, new Date(), Thread.currentThread().getStackTrace(), fullMessage);
  }

  private void write(Collection<Channel> channels, Level level, Date time,
      StackTraceElement[] stack, String message) {
    synchronized (mLock) {
      for (var channel : channels) {
        channel.write(level, time, stack, message);
      }
    }
  }
}
