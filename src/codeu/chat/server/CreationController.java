// Copyright 2021 Google LLC.
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

package codeu.chat.server;

import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.logging.Log;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * A controller focused on creating new data in the model.
 */
public final class CreationController extends ServerController {

  public CreationController(Model model) {
    super(model);
  }

  /**
   * Creates and adds a new user to the model.
   */
  public User newUser(String name) {
    if (name == null || name.strip().isEmpty()) {
      Log.instance.error("Invalid user name: null or empty.");
      return null;
    }

    // Sanitize the name.
    name = name.strip();

    var user = new User(randomUUID(), name, new Date());
    model().users().put(user.id, user);

    Log.instance.info("Created new user: %s [%s].", user.name, user.id);

    return user;
  }

  public Conversation newConversation(String title, UUID owner) {
    if (title == null || title.strip().isEmpty()) {
      return null;
    }

    // Sanitize the title.
    title = title.strip();

    if (owner == null || !model().users().containsKey(owner)) {
      return null;
    }

    var conversation = new Conversation(randomUUID(),
        owner,
        new Date(),
        title,
        Collections.singletonList(owner),
        null);
    model().conversations().put(conversation.id, conversation);

    return conversation;
  }

  public Message newMessage(UUID author, UUID conversation, String body) {
    if (author == null || !model().users().containsKey(author)) {
      return null;
    }

    if (conversation == null || !model().conversations().containsKey(conversation)) {
      return null;
    }

    // Only check if the message is going to be empty, but do not sanitize the body (stripping it)
    // since whitespace will be important to users.
    if (body == null || body.strip().isEmpty()) {
      return null;
    }

    // The conversation that will contain the message.
    var parent = model().conversations().get(conversation);

    var message = new Message(randomUUID(),
        parent.lastMessage,
        new Date(),
        author,
        body);
    model().messages().put(message.id, message);

    // TODO(vaage): Need to update the previous message too, or else we won't be able to find the
    // later messages.

    // Update the conversation to end with our new message. We need to check if this message is the
    // first message. If that is the case, we need to update the first message field too.
    var updatedParent = new Conversation(parent.id,
        parent.owner,
        parent.creation,
        parent.title,
        parent.users,
        message.id);

    model().conversations().put(updatedParent.id, updatedParent);

    return message;
  }
}
