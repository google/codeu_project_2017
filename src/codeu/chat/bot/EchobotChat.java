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
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;

import java.util.Scanner;

// Chat - top-level client application.
public final class EchobotChat {

  private final static Logger.Log LOG = Logger.newLog(EchobotChat.class);

  private final ClientContext clientContext;

  // Constructor - sets up the Chat Application
  public EchobotChat(Controller controller, View view) {
    clientContext = new ClientContext(controller, view);
    init();
  }

  private void init() {
    // Attempt to join the bot channel as a bot.
    final String botname = "Echobot";
    int i = 0;

    // Make sure the user's name is unique
    while (clientContext.user.isUsernameTaken(botname + "-" + i)) {
      i++;
    }

    String uname = botname + "-" + i;
    clientContext.user.addUser(uname);
    clientContext.user.signInUser(uname);

    Uuid uuid = clientContext.user.getCurrent().id;

    // Create a new conversation based on the bot's name and join it.
    Conversation conv = null;
    if (!clientContext.conversation.joinConversation(uname)) {
      conv = clientContext.conversation.startConversation(uname, uuid);
      clientContext.conversation.joinConversation(uname);
    }

    clientContext.message.addMessage(uuid, conv.id, "Magic");
  }
}
