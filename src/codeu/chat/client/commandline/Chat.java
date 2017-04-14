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

  private static final String PROMPT = ">>";

  private final Stack<Panel> panels = new Stack<>();

  public Chat(Context context) {
    this.panels.push(createRootPanel(context));
  }

  public boolean handleCommand(Scanner input) {
    System.out.print(PROMPT);

    final Scanner tokens = new Scanner(input.nextLine().trim());

    final String command = tokens.hasNext() ? tokens.next() : "";

    if ("exit".equals(command)) {
      // The does not want to process anymore commands
      return false;
    }

    if ("back".equals(command)) {
      // Do not allow the root panel to be removed
      if (panels.size() > 1) {
        panels.pop();
      }
    } else if (panels.peek().handleCommand(command, tokens)) {
      // the command was handled
    } else {
      System.out.println("ERROR: Unsupported command");
    }

    return true;
  }

  private Panel createRootPanel(final Context context) {
    return new Panel()
        .register("help", new Panel.Command() {
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
          }})
        .register("u-list", new Panel.Command() {
          @Override
          public void invoke(Scanner args) {
            for (final UserContext user : context.allUsers()) {
              System.out.format(
                  "USER %s (%s)\n",
                  user.user.name,
                  user.user.id);
            }
          }})
        .register("u-add", new Panel.Command() {
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
          }})
        .register("u-sign-in", new Panel.Command() {
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

          private UserContext findUser(String name) {
            for (final UserContext user : context.allUsers()) {
              if (user.user.name.equals(name)) {
                return user;
              }
            }
            return null;
          }});
  }

  private Panel createUserPanel(final UserContext user) {
    return new Panel()
        .register("help", new Panel.Command() {
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
          }})
        .register("c-list", new Panel.Command() {
          @Override
          public void invoke(Scanner args) {
            for (final ConversationContext conversation : user.conversations()) {
              System.out.format(
                  "CONVERSATION %s (%s)\n",
                  conversation.conversation.title,
                  conversation.conversation.id);
            }
          }})
        .register("c-add", new Panel.Command() {
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
          }})
        .register("c-join", new Panel.Command() {
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
          }})
        .register("info", new Panel.Command() {
          @Override
          public void invoke(Scanner args) {
            System.out.println("User Info:");
            System.out.format("  Name : %s\n", user.user.name);
            System.out.format("  Id   : %s\n", user.user.id);
          }});
  }

  private Panel createConversationPanel(final ConversationContext conversation) {
    return new Panel()
        .register("help", new Panel.Command() {
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
          }})
        .register("m-list", new Panel.Command() {
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
          }})
        .register("m-add", new Panel.Command() {
          @Override
          public void invoke(Scanner args) {
            final String message = args.hasNext() ? args.nextLine().trim() : "";
            if (message.length() > 0) {
              conversation.add(message);
            } else {
              System.out.println("ERROR: Messages must contain text");
            }
          }})
        .register("info", new Panel.Command() {
          @Override
          public void invoke(Scanner args) {
            System.out.println("Conversation Info:");
            System.out.format("  Title : %s\n", conversation.conversation.title);
            System.out.format("  Id    : %s\n", conversation.conversation.id);
            System.out.format("  Owner : %s\n", conversation.conversation.owner);
          }});
  }
}
