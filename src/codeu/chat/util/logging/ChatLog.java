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

package codeu.chat.util.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import codeu.logging.Clock;
import codeu.logging.Log;
import codeu.logging.Logger;

// CHAT LOG
//
// The codeu chat log wraps the general codeu log to make it easier to work
// with in the context of the chat app.
//
public final class ChatLog {

  private static final Log LOG = new Log(new Clock() {
    @Override
    public long timeMs() { return System.currentTimeMillis(); }
  });

  // REGISTER
  //
  // Connect an output stream to the log system so that all log messages
  // will be written to the output stream.
  //
  public static void register(OutputStream out) {
    LOG.register(new OutputStreamChannel(out));
  }

  public static <T> Logger logger(Class<T> scope) {
    return LOG.logger(scope.getName());
  }
}
