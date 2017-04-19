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

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import codeu.chat.client.core.Context;
import codeu.chat.client.core.ConversationContext;
import codeu.chat.client.core.MessageContext;
import codeu.chat.client.core.UserContext;

public final class Chat {

  // PANELS
  //
  // We are going to use a stack of panels to track where in the application
  // we are. The command will always be routed to the panel at the top of the
  // stack. When a command wants to go to another panel, it will add a new
  // panel to the top of the stack. When a command wants to go to the previous
  // panel all it needs to do is pop the top panel.
  private final Stack<Panel> panels = new Stack<>();

  public Chat(Context context) {
    this.panels.push(createRootPanel(context));
  }

  // HANDLE COMMAND
  //
  // Take a single line of input and parse a command from it. If the system
  // is willing to take another command, the function will return true. If
  // the system wants to exit, the function will return false.
  //
  public boolean handleCommand(String line) {

    final Scanner tokens = new Scanner(line.trim());

    final String command = tokens.hasNext() ? tokens.next() : "";

    // Because "exit" and "back" are appliable to every panel, handle
    // those commands here to avoid having to implement them for each
    // panel.

    if ("exit".equals(command)) {
      // The user does not want to process anymore commands
      return false;
    }

    // Do not allow the root panel to be removed so make sure there
    // are enough panels before popping.
    if ("back".equals(command) && panels.size() > 1) {
      panels.pop();
      return true;
    }

    if (panels.peek().handleCommand(command, tokens)) {
      // the command was handled
      return true;
    }

    // If we get to here it means that the command was not correctly handled
    // so we should let the user know. Still return true as we want to continue
    // processing future commands.
    System.out.println("ERROR: Unsupported command");
    return true;
  }

  // CREATE ROOT PANEL
  //
  // Create a panel for the root of the application. Root in this context means
  // the first panel and the only panel that should always be at the bottom of
  // the panels stack.
  //
  // The root panel is for commands that require no specific contextual information.
  // This is before a user has signed in. Most commands handled by the root panel
  // will be user selection focused.
  //
  private Panel createRootPanel(final Context context) {

    final Panel panel = new Panel();

    // HELP
    //
    // Add a help command to the root panel. This will be called when the user
    // enters "help" while on the root panel. This will print explanations for
    // all commands that can be ran while on the root panel.
    //
    panel.register("help", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("ROOT MODE");
        System.out.println("  u-list");
        System.out.println("    List all users.");
        System.out.println("  u-add <name>");
        System.out.println("    Add a new user with the given name.");
        System.out.println("  u-sign-in <name>");
        System.out.println("    Sign in as the user with the given name.");
        System.out.println("  exit");
        System.out.println("    Exit the program.");
      }
    });

    // U-LIST (user list)
    //
    // Add a command to print all users to the root panel. This will be called
    // when the user entes "u-list" while on the root panel. This will print a
    // list of all users registered on the server.
    //
    panel.register("u-list", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        for (final UserContext user : context.allUsers()) {
          System.out.format(
              "USER %s (%s)\n",
              user.user.name,
              user.user.id);
        }
      }
    });

    // U-ADD (add user)
    //
    // Add a command to add a new user to the root panel. This will be called when
    // the user enters "u-add" while on the root panel. This will ask the server to
    // create a new user with the given name. If successful, the user will be
    // logged-in as the new user.
    //
    panel.register("u-add", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        final String name = args.hasNext() ? args.nextLine().trim() : "";
        if (name.length() > 0) {
          final UserContext user = context.create(name);
          if (user == null) {
            System.out.println("ERROR: Failed to create new user");
          } else {
            panels.push(createUserPanel(user));
          }
        } else {
          System.out.println("ERROR: Missing <username>");
        }
      }
    });

    // U-SIGN-IN (sign in user)
    //
    // Add a command to sign in as a user to the root panel. This will be called
    // when the user enters "u-sign-in" while on the root panel. This will sign the
    // user in as the given user.
    //
    panel.register("u-sign-in", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        final String name = args.hasNext() ? args.nextLine().trim() : "";
        if (name.length() > 0) {
          final UserContext user = findUser(name);
          if (user == null) {
            System.out.format("ERROR: Failed to sign in as '%s'\n", name);
          } else {
            panels.push(createUserPanel(user));
          }
        } else {
          System.out.println("ERROR: Missing <username>");
        }
      }

      // Find the first user with the given name and return a user context
      // for that user. If no user is found, the function will return null.
      private UserContext findUser(String name) {
        for (final UserContext user : context.allUsers()) {
          if (user.user.name.equals(name)) {
            return user;
          }
        }
        return null;
      }
    });

    // Now that the panel has all its commands registered, return the panel
    // so that it can be used.
    return panel;
  }

  private Panel createUserPanel(final UserContext user) {

    final Panel panel = new Panel();

    // HELP
    //
    // Add a help command to the user panel. This will be called when the user
    // enters "help" while on the user panel. This will print explanations for
    // all commands that can be ran while on the user panel.
    //
    panel.register("help", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("USER MODE");
        System.out.println("  c-list");
        System.out.println("    List all conversation that the current user can interact with.");
        System.out.println("  c-add <title>");
        System.out.println("    Add a new conversation with the given title and join it as the current user.");
        System.out.println("  c-join <title>");
        System.out.println("    Join the conversation as the current user.");
        System.out.println("  info");
        System.out.println("    Display all info for the current user");
        System.out.println("  back");
        System.out.println("    Go back to ROOT MODE.");
        System.out.println("  exit");
        System.out.println("    Exit the program.");
      }
    });

    // C-LIST (list conversations)
    //
    // Add a command to print all conversations to the user panel. This will be called
    // when the user enters "c-list" while on the user panel. This will print the
    // name and id of every conversation on the server.
    //
    panel.register("c-list", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        for (final ConversationContext conversation : user.conversations()) {
          System.out.format(
              "CONVERSATION %s (%s)\n",
              conversation.conversation.title,
              conversation.conversation.id);
        }
      }
    });

    // C-ADD (add conversation)
    //
    // Add a command to add a coversation to the user panel. This will be called
    // when the user enters "c-add" while on the user panel. This will ask the
    // server to create a new conversation with the given name. If successful
    // the user will join the conversation.
    //
    panel.register("c-add", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        final String name = args.hasNext() ? args.nextLine().trim() : "";
        if (name.length() > 0) {
          final ConversationContext conversation = user.start(name);
          if (conversation == null) {
            System.out.println("ERROR: Failed to create new conversation");
          } else {
            panels.push(createConversationPanel(conversation));
          }
        } else {
          System.out.println("ERROR: Missing <title>");
        }
      }
    });

    // C-JOIN (join conversation)
    //
    // Add a command to joing a converation to the user panel. This will be called
    // when the user enters "c-join" while on the user panel. This will look for the
    // first conversation with the given name and the transition to the conversation
    // panel for that conversation.
    //
    panel.register("c-join", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        final String name = args.hasNext() ? args.nextLine().trim() : "";
        if (name.length() > 0) {
          final ConversationContext conversation = find(name);
          if (conversation == null) {
            System.out.format("ERROR: No conversation with name '%s'\n", name);
          } else {
            panels.push(createConversationPanel(conversation));
          }
        } else {
          System.out.println("ERROR: Missing <title>");
        }
      }

      private ConversationContext find(String title) {
        for (final ConversationContext conversation : user.conversations()) {
          if (title.equals(conversation.conversation.title)) {
            return conversation;
          }
        }
        return null;
      }
    });

    // INFO
    //
    // Add a command to print infor about the current context to the user panel. This
    // will be called when the user enters "info" while on the user panel. This will
    // print all the information about the sign-ined user.
    //
    panel.register("info", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("User Info:");
        System.out.format("  Name : %s\n", user.user.name);
        System.out.format("  Id   : %s\n", user.user.id);
      }
    });

    // Now that the panel has all its commands registered, return the panel
    // so that it can be used.
    return panel;
  }

  private Panel createConversationPanel(final ConversationContext conversation) {

    final Panel panel = new Panel();

    // HELP
    //
    // Add a help command to the conversation panel. This will be called when the user
    // enters "help" while on the conversation panel. This will print explanations for
    // all commands that can be ran while on the conversation panel.
    //
    panel.register("help", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("USER MODE");
        System.out.println("  m-list");
        System.out.println("    List all messages in the current conversation.");
        System.out.println("  m-add <title>");
        System.out.println("    Add a new message to the current conversation as thecurrent user.");
        System.out.println("  info");
        System.out.println("    Display all info about the current conversation.");
        System.out.println("  back");
        System.out.println("    Go back to USER MODE.");
        System.out.println("  exit");
        System.out.println("    Exit the program.");
      }
    });

    // M-LIST (list messages)
    //
    // Add a list messages command to the conversation panel. This will be called
    // when the user enters "m-list" while on the conversation panel. This will
    // print every message in the conversation.
    //
    panel.register("m-list", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("--- start of conversation ---");
        for (MessageContext message = conversation.firstMessage();
                            message != null;
                            message = message.next()) {
          System.out.println();
          System.out.format("USER : %s\n", message.message.author);
          System.out.format("SENT : %s\n", message.message.creation);
          System.out.println();
          System.out.println(message.message.content);
          System.out.println();
        }
        System.out.println("---  end of conversation  ---");
      }
    });

    // M-ADD (add message)
    //
    // Add an add message command to the conversation panel. This will be called
    // when the user enters "m-add" while on the conversation panel. This will ask
    // the server to add a new message to the current conversation from the current
    // user.
    //
    panel.register("m-add", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        final String message = args.hasNext() ? args.nextLine().trim() : "";
        if (message.length() > 0) {
          conversation.add(message);
        } else {
          System.out.println("ERROR: Messages must contain text");
        }
      }
    });

    // INFO
    //
    // Add a command to print info about the current conversation to the conversation
    // panel. This will be called when the user enters "info" while on the converastion
    // panel. This will print all the infromation about the current conversation including
    // the title, id, and owner.
    //
    panel.register("info", new Panel.Command() {
      @Override
      public void invoke(Scanner args) {
        System.out.println("Conversation Info:");
        System.out.format("  Title : %s\n", conversation.conversation.title);
        System.out.format("  Id    : %s\n", conversation.conversation.id);
        System.out.format("  Owner : %s\n", conversation.conversation.owner);
      }
    });

    // Now that the panel has all its commands registered, return the panel
    // so that it can be used.
    return panel;
  }
}
