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

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class RawControllerTest {

  private Model model;
  private RawController controller;

  private Uuid userId;
  private Uuid conversationId;
  private Uuid messageId;

  @Before
  public void doBefore() {
    model = new Model();
    controller = new Controller(Uuid.NULL, model);

    userId = new Uuid(1);
    conversationId = new Uuid(2);
    messageId = new Uuid(3);
  }

  @Test
  public void testAddUser() {

    final User user = controller.newUser(userId, "user", Time.now());

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));
  }

  @Test
  public void testAddConversation() {

    final User user = controller.newUser(userId, "user", Time.now());

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));

    final ConversationHeader conversation = controller.newConversation(
        conversationId,
        "conversation",
        user.id,
        Time.now());

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);
    assertTrue(
        "Check that the conversation has the correct id",
        Uuid.equals(conversation.id, conversationId));
  }

  @Test
  public void testAddMessage() {

    final User user = controller.newUser(userId, "user", Time.now());

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));

    final ConversationHeader conversation = controller.newConversation(
        conversationId,
        "conversation",
        user.id,
        Time.now());

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);
    assertTrue(
        "Check that the conversation has the correct id",
        Uuid.equals(conversation.id, conversationId));

    final Message message = controller.newMessage(
        messageId,
        user.id,
        conversation.id,
        "Hello World",
        Time.now());

    assertFalse(
        "Check that the message has a valid reference",
        message == null);
    assertTrue(
        "Check that the message has the correct id",
        Uuid.equals(message.id, messageId));
  }
}
