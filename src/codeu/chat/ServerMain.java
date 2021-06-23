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
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ServerConnectionSource;
import codeu.chat.util.logging.Log;
import java.io.IOException;

final class ServerMain {

  public static void main(String[] args) {
    Log.instance.info("============================= START OF LOG =============================");

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

      Log.instance.info("Starting server...");
      runServer(id, secret, serverSource);

    } catch (IOException ex) {

      Log.instance.error("Failed to establish connections: %s", ex.getMessage());

    }
  }

  private static void runServer(Uuid id, byte[] secret, ConnectionSource serverSource) {

    final Server server = new Server(id, secret);

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
