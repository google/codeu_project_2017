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


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
// import com.codeu.MyResource;
// import com.codeu.MyCORSFilter;



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

  // Base URI the Grizzly HTTP server will listen on (for the web client)
  public static final String BASE_URI = "http://localhost:8080/myapp/";

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   * This is for the web client to be able to access the java backend
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer() {
    // create a resource config that scans for JAX-RS resources and providers
    // in this com.codu package
    final ResourceConfig rc = new ResourceConfig().packages("com.codeu");

    // this line allows programs running on other ports to read what is 
    // being sent from here (not good in the real world for cyber safety
    // reasons, but impt while developing)
    rc.register(new MyCORSFilter());

    // create and start a new instance of grizzly http server
    // exposing the Jersey application at BASE_URI
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
  }


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


    // Starting server that can be accessed through get and post requests by the web client
    final HttpServer webserver = startServer();
    System.out.println(String.format("Jersey app started with WADL available at "
            + "%sapplication.wadl\n", BASE_URI));
    // Server is not properly closed for now only
    // System.in.read();
    // webserver.stop();


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





