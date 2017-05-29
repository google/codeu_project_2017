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

package codeu.chat.bot;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.client.commandline.ListNavigator;
import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;

import java.util.List;
import java.util.Scanner;

// Chat - top-level client application.
public final class EchobotChat {

  private final static Logger.Log LOG = Logger.newLog(EchobotChat.class);

  private final ClientContext clientContext;

  private String uname;
  private Uuid uuid;
  private Conversation conv;

  // Constructor - sets up the Chat Application
  public EchobotChat(Controller controller, View view) {
    clientContext = new ClientContext(controller, view);
    init();
  }

  /**
   * This function handles the initialization of this bot, like joining the server, joining the conversation, etc.
   */
  private void init() {
    // Attempt to join the bot channel as a bot.
    final String name = "Echobot";
    int i = 0;

    // Make sure the user's name is unique
    while (clientContext.user.isUsernameTaken(name + "-" + i)) {
      i++;
    }

    uname = name + "-" + i;
    clientContext.user.addUser(uname);
    clientContext.user.signInUser(uname);

    uuid = clientContext.user.getCurrent().id;

    // Create a new conversation based on the bot's name and join it.
    if (!clientContext.conversation.joinConversation(uname)) {
      conv = clientContext.conversation.startConversation(uname, uuid);
      clientContext.conversation.joinConversation(uname);
    }

    if (conv != null) clientContext.message.addMessage(uuid, conv.id, "Welcome to Echobot's conversation!");
    System.out.println("Startup completed.");
  }

  /**
   * This function returns the last user message received in the current channel.
   * @return
   */
  public String getLastUserInput() {
    List<Message> contents = clientContext.message.getConversationContents(clientContext.conversation.getCurrent());

    // If there aren't any messages, there aren't any user input.
    if (contents.isEmpty()) return null;

    // If the last message is not from another user, it's still null
    if (!contents.get(contents.size() - 1).author.equals(clientContext.user.getCurrent().id)) return null;

    return contents.get(contents.size() - 1).content;
  }

  public void addMessage(String msg) {
    clientContext.message.addMessage(clientContext.user.getCurrent().id,
            clientContext.conversation.getCurrent().id,
            msg);
  }
}
