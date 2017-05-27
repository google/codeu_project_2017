// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.util;

import java.io.IOException;
import java.util.logging.Level;

public final class Logger {

  public interface Log {

    void verbose(String message, Object... params);

    void info(String message, Object... params);

    void warning(String message, Object... params);

    void error(String message, Object... params);
    void error(Throwable error, String message, Object... params);

  }

  private static final java.util.logging.Logger logger =
      java.util.logging.Logger.getLogger("codeu.chat");

  static {
    logger.setLevel(java.util.logging.Level.INFO);

    // Stop this logger from sending its messages up to the root. This will
    // make our logger the new root logger.
    logger.setUseParentHandlers(false);
  }

  public static void enableFileOutput(String file) throws IOException {

    final java.util.logging.Handler handler =
        new java.util.logging.FileHandler(file, true /* append */);
    handler.setFormatter(new java.util.logging.SimpleFormatter());
    logger.addHandler(handler);
  }

  public static void enableConsoleOutput() {

    final java.util.logging.Handler handler =
        new java.util.logging.ConsoleHandler();
    handler.setFormatter(new java.util.logging.SimpleFormatter());
    logger.addHandler(handler);
  }

  public static Log newLog(Class<?> c) {

    final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(c.getName());

    // Note: This Logger calls the standard java Logger, so the class/method lookup
    // needs to go one level deeper.  Otherwise it will appear that all the log
    // calls are coming from this object. Use logp() (log precise) to do this.
    return new Log() {

      @Override
      public void verbose(String message, Object... params) {
        log.logp(java.util.logging.Level.FINE,
            Thread.currentThread().getStackTrace()[2].getClassName(),
            Thread.currentThread().getStackTrace()[2].getMethodName(),
            String.format(message, params));
      }

      @Override
      public void info(String message, Object... params) {
        log.logp(java.util.logging.Level.INFO,
            Thread.currentThread().getStackTrace()[2].getClassName(),
            Thread.currentThread().getStackTrace()[2].getMethodName(),
            String.format(message, params));
      }

      @Override
      public void warning(String message, Object... params) {
        log.logp(java.util.logging.Level.WARNING,
                 Thread.currentThread().getStackTrace()[2].getClassName(),
                 Thread.currentThread().getStackTrace()[2].getMethodName(),
                 String.format(message, params));
      }

      @Override
      public void error(String message, Object... params) {
        log.logp(java.util.logging.Level.SEVERE,
                 Thread.currentThread().getStackTrace()[2].getClassName(),
                 Thread.currentThread().getStackTrace()[2].getMethodName(),
                 String.format(message, params));
      }

      @Override
      public void error(Throwable error, String message, Object... params) {
        log.logp(java.util.logging.Level.SEVERE,
                 Thread.currentThread().getStackTrace()[2].getClassName(),
                 Thread.currentThread().getStackTrace()[2].getMethodName(),
                 String.format(message, params), error);
      }
    };
  }
}
