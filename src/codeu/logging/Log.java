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

package codeu.logging;

public final class Log {

  private final OutputBuilder output = new OutputBuilder();
  private final Clock clock;

  public Log(Clock clock) {
    this.clock = clock;
  }

  public Logger logger(final String name) {

    return new Logger() {

      public void verbose(String message, Object... params) {
        addMessage("V", String.format(message, params));
      }

      public void info(String message, Object... params) {
        addMessage("I", String.format(message, params));
      }

      public void warning(String message, Object... params) {
        addMessage("W", String.format(message, params));
      }

      public void warning(Throwable error, String message, Object... params) {
        addMessage("W", String.format(message, params), error);
      }

      public void error(String message, Object... params) {
        addMessage("E", String.format(message, params));
      }

      public void error(Throwable error, String message, Object... params) {
        addMessage("E", String.format(message, params), error);
      }

      private void addMessage(String mode, String message) {

        // Get the time once so that it will be the same for all lines
        final long time = clock.timeMs();

        synchronized(output) {

          output.append(buildLine(mode, System.currentTimeMillis(), message));

          output.push();
        }
      }

      private void addMessage(String mode, String message, Throwable error) {

        // Get the time once so that it will be the same for all lines
        final long time = clock.timeMs();

        synchronized(output) {

          output.append(buildLine(mode, time, message));
          output.append(buildLine(mode, time, String.format("    %s", error.toString())));
          for (final StackTraceElement element : error.getStackTrace()) {
            output.append(buildLine(mode, time, String.format("    %s", element.toString())));
          }

          output.push();
        }
      }

      private String buildLine(String mode, long time, String message) {
        return String.format("[ %s | %d ms ] %s : %s", mode, time, name, message);
      }
    };
  }

  public void register(Channel channel) {
    synchronized(output) {
      output.register(channel);
    }
  }
}

