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
//

package codeu.chat;

import java.io.IOException;
import java.io.File;

import codeu.chat.server.Server;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ServerConnectionSource;
import codeu.chat.util.logging.ChatLog;
import codeu.logging.Logger;

final class ServerMain {

  private static final Logger LOG = ChatLog.logger(ServerMain.class);

  static {
    // Have the server write to std out.
    ChatLog.register(System.out);
  }

  public static void main(String[] args) {

    int port = -1;

    try {
      port = Integer.parseInt(args[0]);
    } catch (Exception ex) {
      LOG.error(ex, "Failed to parse port from %s", args[0]);
      System.exit(1);
    }

    // This is the directory where it is safe to store data accross runs
    // of the server.
    File persistentPath = null;

    try {
      persistentPath = new File(args[1]);
    } catch (Exception ex) {
      LOG.error(ex, "Failed to parse persistent path from %s", args[1]);
      System.exit(1);
    }

    if (!persistentPath.isDirectory()) {
      LOG.error("Persistent path %s is not a directory", args[1]);
      System.exit(1);
    }

    try (
        final ConnectionSource serverSource = ServerConnectionSource.forPort(port);
    ) {

      LOG.verbose("Bound to port %d", port);

      final Server server = new Server();
      LOG.verbose("Successfully create server instance");

      while (true) {
        try {

          LOG.verbose("Waiting for connection...");
          final Connection connection = serverSource.connect();
          LOG.verbose("Connection established.");

          server.handleConnection(connection);

        } catch (IOException ex) {
          LOG.error(ex, "Failed to connect to incoming connection");
        }
      }
    } catch (IOException ex) {
      LOG.error(ex, "Failed to bind to port %d", port);
    }
  }
}
