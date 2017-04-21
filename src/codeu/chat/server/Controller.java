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
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.server.authentication.Authentication;
import codeu.chat.authentication.AuthenticationCode;

public final class Controller implements RawController, BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);

  private final Model model;
  private final Uuid.Generator uuidGenerator;

  private final Authentication authentication;

  public Controller(Uuid serverId, Model model, Authentication authentication) {
    this.model = model;
    this.uuidGenerator = new RandomUuidGenerator(serverId, System.currentTimeMillis());
    this.authentication = authentication;
  }

  @Override
  public Message newMessage(Uuid author, Uuid token, Uuid conversation, String body) {
    if (!checkToken(author, token)) return null;
    return newMessage(createId(), author, conversation, body, Time.now());
  }

  @Override
  public int newUser(String username, String password) {
    return newUser(username, password, Time.now());
  }

  @Override
  public User login(String username, String password) {
    return login(createId(), username, password, Time.now());
  }

  @Override
  public Conversation newConversation(String title, Uuid owner, Uuid token) {
    if (!checkToken(owner, token)) return null;
    return newConversation(createId(), title, owner, Time.now());
  }

  @Override
  public Message newMessage(Uuid id, Uuid author, Uuid conversation, String body, Time creationTime) {

    final User foundUser = model.userById().first(author);
    final Conversation foundConversation = model.conversationById().first(conversation);

    Message message = null;

    if (foundUser != null && foundConversation != null && isIdFree(id)) {

      message = new Message(id, Uuid.NULL, Uuid.NULL, creationTime, author, body);
      model.add(message);
      LOG.info("Message added: %s", message.id);

      // Find and update the previous "last" message so that it's "next" value
      // will point to the new message.

      if (Uuid.equals(foundConversation.lastMessage, Uuid.NULL)) {

        // The conversation has no messages in it, that's why the last message is NULL (the first
        // message should be NULL too. Since there is no last message, then it is not possible
        // to update the last message's "next" value.

      } else {
        final Message lastMessage = model.messageById().first(foundConversation.lastMessage);
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

      if (!foundConversation.users.contains(foundUser)) {
        foundConversation.users.add(foundUser.id);
      }
    }

    return message;
  }

  @Override
  public int newUser(String username, String password, Time creationTime) {
    // Attempt to create the new user.
    int result = authentication.register(username, password);
    LOG.info(
        "newUser result (user.name=%s user.time=%s result=%d)",
        username,
        creationTime,
        result);

    return result;
  }

  @Override
  public User login(Uuid id, String username, String password, Time creationTime) {
    User user = null;

    // Attempt to login.
    int result = authentication.login(username, password);
    if (result == AuthenticationCode.SUCCESS) {
      LOG.info(
          "login success (user.id=%s user.name=%s user.time=%s)",
          id,
          username,
          creationTime);

      // Create the new user.
      user = new User(id, username, creationTime);
      user.token = createId();
      model.add(user);
    } else {
      LOG.info(
          "login fail (user.id=%s user.name=%s user.time=%s result=%d)",
          id,
          username,
          creationTime,
          result);
    }

    return user;
  }

  @Override
  public Conversation newConversation(Uuid id, String title, Uuid owner, Time creationTime) {

    final User foundOwner = model.userById().first(owner);

    Conversation conversation = null;

    if (foundOwner != null && isIdFree(id)) {
      conversation = new Conversation(id, owner, creationTime, title);
      model.add(conversation);

      LOG.info("Conversation added: " + conversation.id);
    }

    return conversation;
  }

  /**
   * Verify that a user matches a given token.
   *
   * @param uuid The UUID of the user.
   * @param token The token.
   *
   * @return Whether the user matches the given token.
   */
  public boolean checkToken(Uuid uuid, Uuid token) {
    User user = model.userById().first(uuid);
    if (user == null) return false;
    if (user.token.equals(token)) return true;
    return false;
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
    return model.messageById().first(id) != null ||
           model.conversationById().first(id) != null ||
           model.userById().first(id) != null ||
           model.userByToken().first(id) != null;
  }

  private boolean isIdFree(Uuid id) { return !isIdInUse(id); }

}
