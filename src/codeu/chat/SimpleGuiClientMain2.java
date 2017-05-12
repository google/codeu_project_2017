// Copyright 2017 Google Inc.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat;

import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.client.simplegui2.MainGui;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.ConnectionSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import java.io.IOException;

public class SimpleGuiClientMain2 extends Application{

  private static final Logger.Log LOG = Logger.newLog(SimpleGuiClientMain2.class);
  private static Stage windowPrime;
  private static String[] arguments;

  public static void main(String [] args) {

      arguments=args;
      launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception{

      windowPrime= primaryStage;

    try {
      Logger.enableFileOutput("chat_simple_gui_client_log.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    LOG.info("Starting chat client...");

    // Start up server connection/interface.

    final RemoteAddress address = RemoteAddress.parse(arguments[0]);

    try (
            final ConnectionSource source = new ClientConnectionSource(address.host, address.port)
    ) {
      final Controller controller = new Controller(source);
      final View view = new View(source);

      LOG.info("Creating client...");

      final MainGui chatGui = new MainGui(controller, view);

      LOG.info("Created client");

      chatGui.start();

      LOG.info("chat client is running.");

    } catch (Exception ex) {
      System.out.println("ERROR: Exception setting up client. Check log for details.");
      LOG.error(ex, "Exception setting up client.");
    }

  }

  private static void runClient(Controller controller, View view) {

    final MainGui chatGui = new MainGui(controller, view);

    LOG.info("Created client");

    chatGui.start();

    LOG.info("chat client is running.");
  }
}
