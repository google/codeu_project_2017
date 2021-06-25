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

package codeu.chat.client.commandline;

import codeu.chat.client.Context;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.logging.Log;
import java.util.Collections;
import java.util.Scanner;

// Chat - top-level client application.
public final class Chat {

  private static final String PROMPT = ">>";

  private final static int PAGE_SIZE = 10;

  private boolean alive = true;

  private final Context mContext = new Context();

  private final ConnectionSource mRemote;

  public Chat(ConnectionSource remote) {
    mRemote = remote;
  }

  // Print help message.
  private static void help() {
    System.out.println("Chat commands:");
    System.out.println("   exit      - exit the program.");
    System.out.println("   help      - this help message.");
    System.out.println("   sign-in <username>  - sign in as user <username>.");
    System.out.println("   sign-out  - sign out current user.");
    System.out.println("   current   - show current user, conversation, message.");
    System.out.println("User commands:");
    System.out.println("   u-add <name>  - add a new user.");
    System.out.println("   u-list-all    - list all users known to system.");
    System.out.println("Conversation commands:");
    System.out.println("   c-add <title>    - add a new conversation.");
    System.out.println("   c-list-all       - list all conversations known to system.");
    System.out.println("   c-select <index> - select conversation from list.");
    System.out.println("Message commands:");
    System.out.println("   m-add <body>     - add a new message to the current conversation.");
    System.out.println("   m-show-last      - shows the last message in the current conversation.");
  }

  // Prompt for new command.
  private void promptForCommand() {
    System.out.print(PROMPT);
  }

  // Parse and execute a single command.
  private void doOneCommand(Scanner lineScanner) {

    final Scanner tokenScanner = new Scanner(lineScanner.nextLine());
    if (!tokenScanner.hasNext()) {
      return;
    }
    final String token = tokenScanner.next();

    if (token.equals("exit")) {

      alive = false;

    } else if (token.equals("help")) {

      help();

    } else if (token.equals("sign-in")) {

      if (!tokenScanner.hasNext()) {
        System.out.println("ERROR: No user name supplied.");
      } else {
        var name = tokenScanner.nextLine().trim();

        var view = new View(mRemote);

        User found = null;
        for (var user : view.getAllUsers()) {
          if (user.name.equals(name)) {
            found = user;
            break;
          }
        }

        if (found == null) {
          System.out.format("No user with the name \"%s\" found. Create a new account?\n");
        } else {
          mContext.changeUser(found.id);
        }
      }

    } else if (token.equals("sign-out")) {

      if (mContext.user() == null) {
        System.out.println("ERROR: Could not sign-out (were you signed-in?)");
      }

      mContext.changeUser(null);

    } else if (token.equals("current")) {

      showContext();

    } else if (token.equals("u-add")) {
      if (!tokenScanner.hasNext()) {
        System.out.println("ERROR: Username not supplied.");
      } else {
        var name = tokenScanner.nextLine().trim();

        var controller = new Controller(mRemote);
        var user = controller.newUser(name);

        if (user == null) {
          System.out.format("ERROR: Failed to create user with name \"%s\".", name);
        } else {
          mContext.changeUser(user.id);
        }
      }
    } else if (token.equals("u-list-all")) {
      var view = new View(mRemote);

      System.out.println("Users:");
      for (var user : view.getAllUsers()) {
        var active = user.id.equals(mContext.user());

        if (active) {
          System.out.format("  %s [%s] (active)\n", user.name, user.id);
        } else {
          System.out.format("  %s [%s]\n", user.name, user.id);
        }
      }
    } else if (token.equals("c-add")) {

      if (mContext.user() == null) {
        System.out.println("ERROR: Not signed in.");
      } else if (!tokenScanner.hasNext()) {
        System.out.println("ERROR: Conversation title not supplied.");
      } else {
        final String title = tokenScanner.nextLine().trim();

        var controller = new Controller(mRemote);
        controller.newConversation(title, mContext.user());
      }

    } else if (token.equals("c-list-all")) {

      var view = new View(mRemote);

      System.out.println("Conversations:");

      for (var conversation : view.getAllConversations()) {
        boolean active = conversation.id.equals(mContext.conversation());

        if (active) {
          System.out.format("  \"%s\" [%s] (active)\n", conversation.title, conversation.id);
        } else {
          System.out.format("  \"%s\" [%s]\n", conversation.title, conversation.id);
        }
      }

    } else if (token.equals("c-select")) {

      selectConversation(lineScanner);

    } else if (token.equals("m-add")) {

      if (mContext.user() == null) {
        System.out.println("ERROR: Not signed in.");
      } else if (mContext.conversation() == null) {
        System.out.println("ERROR: No conversation selected.");
      } else if (!tokenScanner.hasNext()) {
        System.out.println("ERROR: Message body not supplied.");
      } else {
        var controller = new Controller(mRemote);
        controller.newMessage(mContext.user(),
            mContext.conversation(),
            tokenScanner.nextLine().trim());
      }

    } else if (token.equals("m-show-last")) {

      if (mContext.conversation() == null) {
        System.out.println("ERROR: No conversation selected.");
      } else {
        var view = new View(mRemote);
        var conversations = view
            .getConversations(Collections.singletonList(mContext.conversation()));

        if (conversations.size() == 1) {
          var conversation = conversations.stream().findFirst().get();
          var messages = view.getMessages(Collections.singleton(conversation.lastMessage));

          if (messages.size() == 1) {
            showMessage(messages.stream().findFirst().get());
          }
        }
      }

    } else {

      System.out.format("Command not recognized: %s\n", token);
      System.out.format("Command line rejected: %s%s\n", token,
          (tokenScanner.hasNext()) ? tokenScanner.nextLine() : "");
      System.out.println("Type \"help\" for help.");
    }
    tokenScanner.close();
  }

  /**
   * Show information about the current user and conversation.
   */
  private void showContext() {
    var view = new View(mRemote);

    boolean hadUserInfo = false;

    if (mContext.user() != null) {
      var users = view.getUsers(Collections.singletonList(mContext.user()));

      if (users.size() == 1) {
        var user = users.stream().findFirst().get();

        System.out.println("User:");
        System.out.format("  id:      %s\n", user.id);
        System.out.format("  name:    %s\n", user.name);
        System.out.format("  created: %s\n", user.creation);

        hadUserInfo = true;
      } else {
        Log.instance.error("The server returned an unexpected number of users: %d", users.size());
      }
    } else {
      Log.instance.error("No active user.");
    }

    boolean hadConversationInfo = false;

    if (mContext.conversation() != null) {
      var conversations = view.getConversations(Collections.singletonList(mContext.conversation()));

      if (conversations.size() == 1) {
        var conversation = conversations.stream().findFirst().get();

        System.out.println("Conversation:");
        System.out.format("  id:      %s\n", conversation.id);
        System.out.format("  title:   %s\n", conversation.title);
        System.out.format("  created: %s\n", conversation.creation);

        hadConversationInfo = true;
      } else {
        Log.instance.error("The server returned an unexpected number of conversations: %d",
            conversations.size());
      }
    } else {
      Log.instance.info("No active conversation.");
    }

    if (!hadUserInfo) {
      System.out.println("User: no signed-in user");
    }

    if (!hadConversationInfo) {
      System.out.println("Conversation: no active conversation");
    }
  }

  private void showMessage(Message message) {
    var view = new View(mRemote);

    var authors = view.getUsers(Collections.singletonList(message.author));
    var author = authors.size() == 1 ? authors.stream().findFirst().get() : null;

    System.out.format("Message:\n");
    System.out.format("  from: %s [%s]\n", author == null ? "<unknown>" : author.name,
        author == null ? "?" : author.id);
    System.out.format("  date: %s\n", message.creation);
    System.out.println();
    System.out.println(message.content);
    System.out.println();
  }

  public boolean handleCommand(Scanner lineScanner) {

    try {
      promptForCommand();
      doOneCommand(lineScanner);
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during command processing. Check log for details.");
      Log.instance.error("Exception during command processing: %s", ex.getMessage());
    }

    // "alive" may have been set to false while executing a command. Return
    // the result to signal if the user wants to keep going.

    return alive;
  }

  public void selectConversation(Scanner lineScanner) {

    var view = new View(mRemote);

    var options = view.getAllConversations();

    if (options.size() == 0) {
      System.out.println("There are no conversations, maybe start a new one?");
      return;
    }

    System.out.format("Selection contains %d entries.\n", options.size());

    var navigator = new ListNavigator<>(
        new ConversationSummaryListView(),
        options,
        lineScanner,
        PAGE_SIZE);

    if (!navigator.chooseFromList()) {
      System.out.format("OK. No new conversation was selected.\n");
    }

    var selection = navigator.getSelectedChoice();

    mContext.changeConversation(selection.id);
    System.out.format("OK. Conversation \"%s\" selected.\n", selection.title);
  }
}
