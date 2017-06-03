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

package codeu.chat;

import codeu.chat.bot.tictactoe.TicTacToeChat;
import codeu.chat.bot.tictactoe.TicTacToeGame;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.common.Message;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.ConnectionSource;

import java.io.IOException;

final class TicTacToeBot {

  private static final Logger.Log LOG = Logger.newLog(TicTacToeBot.class);

  public static void main(String [] args) {

    try {
      Logger.enableFileOutput("chat_bot.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    LOG.info("Starting bot client...");

    final RemoteAddress address = RemoteAddress.parse(args[0]);

    final ConnectionSource source = new ClientConnectionSource(address.host, address.port);
    final Controller controller = new Controller(source);
    final View view = new View(source);

    LOG.info("Creating bot...");
    final TicTacToeChat chat = new TicTacToeChat(controller, view);

    LOG.info("Created bot");

    TicTacToeGame game = new TicTacToeGame();

    while (true) {
      while (game.waitForAction(chat)) {
        // waitForAction will handle the operations.
        try {
          Thread.sleep(1000);
        } catch(InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      }

      // At this point, the game has ended.
      // The outer while-loop will start a new game.
      game.init();
    }
  }
}
