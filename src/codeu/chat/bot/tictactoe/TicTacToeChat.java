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

package codeu.chat.bot.tictactoe;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;

import java.util.List;

// Chat - top-level client application.
public final class TicTacToeChat {

  private final static Logger.Log LOG = Logger.newLog(TicTacToeChat.class);

  private final ClientContext clientContext;

  private Conversation conv;

  private String uname;
  private Uuid uuid;

  // Constructor - sets up the Chat Application
  public TicTacToeChat(Controller controller, View view) {
    clientContext = new ClientContext(controller, view);
    init();
  }

  /**
   * This function handles the initialization of this bot, like joining the server, joining the conversation, etc.
   */
  public void init() {
    // If the bot already existed before, just use the old informations.
    if (uname == null) {
      // Attempt to join the bot channel as a bot.
      final String name = "TicTacToe";
      int i = 0;

      // Make sure the user's name is unique
      while (clientContext.user.isUsernameTaken(name + "-" + i)) {
        i++;
      }

      uname = name + "-" + i;
      clientContext.user.addUser(uname);
      clientContext.user.signInUser(uname);

      // Create a new conversation based on the bot's name and join it.
      uuid = clientContext.user.getCurrent().id;
      if (!clientContext.conversation.joinConversation(uname)) {
        conv = clientContext.conversation.startConversation(uname, uuid);
        clientContext.conversation.joinConversation(uname);
      }
    }

    String startupMsg = "--- Welcome to " + uname + "'s Conversation! ---\n" +
            "Type \"start\" to start playing Tic Tac Toe!";

    if (conv != null) clientContext.message.addMessage(uuid, conv.id, startupMsg);
    System.out.println("Startup completed.");
  }

  /**
   * This function returns the last user message received in the current channel.
   * @return
   */
  public Message getLastUserMessage() {
    clientContext.conversation.updateAllConversations(true);
    List<Message> contents = clientContext.message.getConversationContents(clientContext.conversation.getCurrent());

    // If there aren't any messages, there aren't any user input.
    if (contents.isEmpty()) return null;

    Message lastMessage = contents.get(contents.size() - 1);

    // If the last message is not from another user, it's still null
    Uuid msgUuid = lastMessage.author;
    Uuid botUuid = clientContext.user.getCurrent().id;

    if (msgUuid.equals(botUuid)) return null;

    return lastMessage;
  }

  public void addMessage(String msg) {
    clientContext.message.addMessage(clientContext.user.getCurrent().id,
            clientContext.conversation.getCurrent().id,
            msg);
  }
}
