package com.codeu;

import java.io.IOException;

import codeu.chat.common.Relay;
import codeu.chat.common.Secret;
import codeu.chat.server.NoOpRelay;
import codeu.chat.server.RemoteRelay;
import codeu.chat.server.Server;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ServerConnectionSource;



/**
 * Main class.
 * Used to create and start the backend server(s)
 * Much of the code here is copied directly from what was once codeu.chat.ServerMain,
 * with additions to set up connections with a web client.
 * 
 * Web client-related things based off of resources given at: 
 * https://jersey.java.net/documentation/latest/getting-started.html#new-from-archetype
 * 
 * Other, copied code is Copyright 2017 Google Inc. under this license: http://www.apache.org/licenses/LICENSE-2.0
 * 
 */
final class Main {

  private static final Logger.Log LOG = Logger.newLog(Main.class);


  public static void main(String[] args) {

    Logger.enableConsoleOutput();

    try {
      Logger.enableFileOutput("chat_server_log.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    final Uuid id = Uuid.fromString(args[0]);
    final byte[] secret = Secret.parse(args[1]);

    final int myPort = Integer.parseInt(args[2]);

    final RemoteAddress relayAddress = args.length > 3 ?
                                       RemoteAddress.parse(args[3]) :
                                       null;

    try (
        final ConnectionSource serverSource = ServerConnectionSource.forPort(myPort);
        final ConnectionSource relaySource = relayAddress == null ? null : new ClientConnectionSource(relayAddress.host, relayAddress.port)
    ) {

      LOG.info("Starting server...");
      runServer(id, secret, serverSource, relaySource);

    } catch (IOException ex) {

      LOG.error(ex, "Failed to establish connections");

    }
  }

  private static void runServer(Uuid id,
                                byte[] secret,
                                ConnectionSource serverSource,
                                ConnectionSource relaySource) {

    final Relay relay = relaySource == null ?
                        new NoOpRelay() :
                        new RemoteRelay(relaySource);

    final Server server = new Server(id, secret, relay);

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





