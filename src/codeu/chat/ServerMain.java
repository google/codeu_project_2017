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

import codeu.chat.server.Server;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ServerConnectionSource;
import codeu.chat.util.logging.Log;
import java.io.IOException;

final class ServerMain {

  public static void main(String[] args) {
    Log.instance.info("============================= START OF LOG =============================");

    final int myPort = Integer.parseInt(args[0]);

    try (ConnectionSource serverSource = ServerConnectionSource.forPort(myPort)) {

      Log.instance.info("Starting server...");
      runServer(serverSource);

    } catch (IOException ex) {

      Log.instance.error("Failed to establish connections: %s", ex.getMessage());

    }
  }

  private static void runServer(ConnectionSource serverSource) {

    final Server server = new Server();

    Log.instance.info("Created server.");

    while (true) {

      try {

        Log.instance.info("Established connection...");
        final Connection connection = serverSource.connect();
        Log.instance.info("Connection established.");

        server.handleConnection(connection);

      } catch (IOException ex) {
        Log.instance.error("Failed to establish connection: %s", ex.getMessage());
      }
    }
  }
}
