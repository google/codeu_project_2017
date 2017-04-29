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

package codeu.chat.server;

import java.util.Collection;

import codeu.chat.common.BasicController;
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.Message;
import codeu.chat.common.RandomUuidGenerator;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.logging.ChatLog;
import codeu.logging.Logger;

public final class Controller implements RawController, BasicController {

  private static final Logger LOG = ChatLog.logger(Controller.class);

  private final Model model;
  private final Uuid.Generator uuidGenerator;

  public Controller(Model model) {
    this.model = model;
    this.uuidGenerator = new RandomUuidGenerator(null, System.currentTimeMillis());
  }

  @Override
  public Message newMessage(Uuid author, Uuid conversation, String body) {
    return newMessage(createId(), author, conversation, body, Time.now());
  }

  @Override
  public User newUser(String name) {
    return newUser(createId(), name, Time.now());
  }

  @Override
  public ConversationHeader newConversation(String title, Uuid owner) {
    return newConversation(createId(), title, owner, Time.now());
  }

  @Override
  public Message newMessage(Uuid id, Uuid author, Uuid conversation, String body, Time creationTime) {

    LOG.verbose("Making new message with id=%s, author=%s, and conversation=%s.", id, author, conversation);

    final User foundUser = model.users.get(author);
    final ConversationPayload foundConversation = model.payloads.get(conversation);

    Message message = null;

    if (foundUser == null) {

      LOG.error("Failed to make new message as no user with id %s was found", author);

    } else if (foundConversation == null) {

      LOG.error("Failed to make new message as no conversation with id %s was found", conversation);

    } else if (isIdInUse(id)) {

      LOG.error("Failed to make new message as id %s is already in use", id);

    } else {

      message = new Message(id, Uuid.NULL, Uuid.NULL, creationTime, author, body);

      LOG.info("Message added: %s", message.id);
      model.messages.put(id, message);

      // Find and update the previous "last" message so that it's "next" value
      // will point to the new message.

      if (Uuid.equals(foundConversation.lastMessage, Uuid.NULL)) {

        // The conversation has no messages in it, that's why the last message is NULL (the first
        // message should be NULL too. Since there is no last message, then it is not possible
        // to update the last message's "next" value.

      } else {
        final Message lastMessage = model.messages.get(foundConversation.lastMessage);
        lastMessage.next = message.id;
      }

      // If the first message points to NULL it means that the conversation was empty and that
      // the first message should be set to the new message. Otherwise the message should
      // not change.

      foundConversation.firstMessage =
          Uuid.equals(foundConversation.firstMessage, Uuid.NULL) ?
          message.id :
          foundConversation.firstMessage;

      // Update the conversation to point to the new last message as it has changed.

      foundConversation.lastMessage = message.id;

      LOG.info("Created new message with id=%s, author=%s, and conversation=%s.", id, author, conversation);
    }

    return message;
  }

  @Override
  public User newUser(Uuid id, String name, Time creationTime) {

    LOG.verbose("Making new user with id=%s and name='%s'.", id, name);

    User user = null;

    if (isIdInUse(id)) {

      LOG.error("Cannot create user with id %s as it is already in use.", id);

    } else {

      user = new User(id, name, creationTime);
      model.users.put(id, user);

      LOG.info("Created user with id=%s and name='%s'.", id, name);

    }

    return user;
  }

  @Override
  public ConversationHeader newConversation(Uuid id, String title, Uuid owner, Time creationTime) {

    LOG.verbose("Making new conversation with id=%s and title='%s'.", id, title);

    final User foundOwner = model.users.get(owner);

    ConversationHeader header = null;
    ConversationPayload payload = null;

    if (foundOwner == null) {

      LOG.error("Failed to find user with id %s to own conversation", owner);

    } else if (isIdInUse(id)) {

      LOG.error("Cannot create conversation with id %s as it is already in use", id);

    } else {

      header = new ConversationHeader(id, owner, creationTime, title);
      payload = new ConversationPayload(id);

      model.headers.put(id, header);
      model.payloads.put(id, payload);

      LOG.info("Created conversation with id=%s, title='%s', and owner=%s.", id, title, owner);
    }

    return header;
  }

  private Uuid createId() {

    Uuid candidate;

    for (candidate = uuidGenerator.make();
         isIdInUse(candidate);
         candidate = uuidGenerator.make()) {

      // Assuming that "randomUuid" is actually well implemented, this
      // loop should never be needed, but just incase make sure that the
      // Uuid is not actually in use before returning it.

    }

    return candidate;
  }

  private boolean isIdInUse(Uuid id) {
    return model.messages.get(id) != null ||
           model.payloads.get(id) != null ||
           model.users.get(id) != null;
  }

  private boolean isIdFree(Uuid id) { return !isIdInUse(id); }

}
