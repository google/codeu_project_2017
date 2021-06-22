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

import codeu.chat.common.Secret;
import codeu.chat.server.Server;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ServerConnectionSource;
import java.io.IOException;

final class ServerMain {

  private static final Logger.Log LOG = Logger.newLog(ServerMain.class);

  public static void main(String[] args) {

    Logger.enableConsoleOutput();

    try {
      Logger.enableFileOutput("chat_server_log.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    final int myPort = Integer.parseInt(args[2]);
    final byte[] secret = Secret.parse(args[1]);

    Uuid id = null;
    try {
      id = Uuid.parse(args[0]);
    } catch (IOException ex) {
      System.out.println("Invalid id - shutting down server");
      System.exit(1);
    }

    try (ConnectionSource serverSource = ServerConnectionSource.forPort(myPort)) {

      LOG.info("Starting server...");
      runServer(id, secret, serverSource);

    } catch (IOException ex) {

      LOG.error(ex, "Failed to establish connections");

    }
  }

  private static void runServer(Uuid id, byte[] secret, ConnectionSource serverSource) {

    final Server server = new Server(id, secret);

    LOG.info("Created server.");

    while (true) {

      try {

        LOG.info("Established connection...");
        final Connection connection = serverSource.connect();
        LOG.info("Connection established.");

        server.handleConnection(connection);

      } catch (IOException ex) {
        LOG.error(ex, "Failed to establish connection.");
      }
    }
  }
}
