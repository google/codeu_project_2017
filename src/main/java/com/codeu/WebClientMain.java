package com.codeu;

import java.io.IOException;
import java.util.Scanner;

import codeu.chat.client.commandline.Chat;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.ConnectionSource;



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

  public static void main(String [] args) {

    try {
      Logger.enableFileOutput("chat_client_log.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    LOG.info("Starting chat client...");

    final RemoteAddress address = RemoteAddress.parse(args[0]);

    final ConnectionSource source = new ClientConnectionSource(address.host, address.port);
    final Controller controller = new Controller(source);
    final View view = new View(source);

    LOG.info("Creating client...");
    final Chat chat = new Chat(controller, view);

    LOG.info("Created client");

    final Scanner input = new Scanner(System.in);

    while (chat.handleCommand(input)) {
      // everything is done in "run"
    }

    input.close();

    LOG.info("chat client has exited.");
  }
}
