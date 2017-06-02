package codeu.chat;

import java.io.IOException;

import codeu.chat.client.Controller;
import codeu.chat.client.maingui.MainGui;
import codeu.chat.client.View;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.ConnectionSource;

final class MainGuiClient {

  private static final Logger.Log LOG = Logger.newLog(MainGuiClient.class);

  public static void main(String [] args) {

    try {
      Logger.enableFileOutput("chat_main_gui_client_log.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    LOG.info("Starting chat client...");

    // Start up server connection/interface.

    final RemoteAddress address = RemoteAddress.parse(args[0]);

    try (
      final ConnectionSource source = new ClientConnectionSource(address.host, address.port)
    ) {
      final Controller controller = new Controller(source);
      final View view = new View(source);

      LOG.info("Creating client...");

      runClient(controller, view);

    } catch (Exception ex) {
      System.out.println("ERROR: Exception setting up client. Check log for details.");
      LOG.error(ex, "Exception setting up client.");
    }
  }

  private static void runClient(Controller controller, View view) {

    final MainGui chatMainGui = new MainGui(controller, view);

    LOG.info("Created client");

    chatMainGui.run();

    LOG.info("chat client is running.");
  }
}
