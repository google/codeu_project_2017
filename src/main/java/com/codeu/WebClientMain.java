package com.codeu;

import java.io.IOException;
import java.util.Scanner;

import codeu.chat.client.angular.WebChat;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.ConnectionSource;


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;


/**
 * Main class for the web client.
 * 
 * Much of the code here is copied directly from what was once codeu.chat.ClientMain,
 * with some other changes.
 * 
 * Web client-related things based off of resources given at: 
 * https://jersey.java.net/documentation/latest/getting-started.html#new-from-archetype
 * 
 * Other, copied code is Copyright 2017 Google Inc. under this license: http://www.apache.org/licenses/LICENSE-2.0
 * 
 */
final class WebClientMain {

  private static final Logger.Log LOG = Logger.newLog(WebClientMain.class);


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



  public static void main(String [] args) {

    try {
      Logger.enableFileOutput("chat_client_log.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    LOG.info("Starting chat client...");

    // final RemoteAddress address = RemoteAddress.parse(args[0]);

    // final ConnectionSource source = new ClientConnectionSource(address.host, address.port);
    // final Controller controller = new Controller(source);
    // final View view = new View(source);

    // LOG.info("Creating client...");
    // final WebChat chat = new WebChat(controller, view);

    // LOG.info("Created client");

    // final Scanner input = new Scanner(System.in);




    // Starting server that can be accessed through get and post requests by the web client
    final HttpServer webserver = startServer();
    System.out.println(String.format("Jersey app started with WADL available at "
            + "%sapplication.wadl\n", BASE_URI));
    // Server is not properly closed for now only
    // System.in.read();
    // webserver.stop();



    while(true){
      
    }



    // while (chat.handleCommand(input)) {
    //   // everything is done in "run"
    // }

    // input.close();

    // LOG.info("chat client has exited.");
  }
}
