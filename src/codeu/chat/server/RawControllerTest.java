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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Time;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class RawControllerTest {

  private Model model;
  private RawController controller;

  private UUID userId;
  private UUID conversationId;
  private UUID messageId;

  @BeforeEach
  public void doBefore() {
    model = new Model();
    controller = new Controller(model);

    userId = new UUID(0, 1);
    conversationId = new UUID(0, 2);
    messageId = new UUID(0, 3);
  }

  @Test
  public void testAddUser() {

    final User user = controller.newUser(userId, "user", Time.now());

    assertNotNull(user, "Check that user has a valid reference");
    assertEquals(user.id, userId, "Check that the user has the correct id");
  }

  @Test
  public void testAddConversation() {

    final User user = controller.newUser(userId, "user", Time.now());

    assertNotNull(user, "Check that user has a valid reference");
    assertEquals(user.id, userId, "Check that the user has the correct id");

    final Conversation conversation = controller.newConversation(
        conversationId,
        "conversation",
        user.id,
        Time.now());

    assertNotNull(conversation, "Check that conversation has a valid reference");
    assertEquals(conversation.id, conversationId, "Check that the conversation has the correct id");
  }

  @Test
  public void testAddMessage() {

    final User user = controller.newUser(userId, "user", Time.now());

    assertNotNull(user, "Check that user has a valid reference");
    assertEquals(user.id, userId, "Check that the user has the correct id");

    final Conversation conversation = controller.newConversation(
        conversationId,
        "conversation",
        user.id,
        Time.now());

    assertNotNull(conversation, "Check that conversation has a valid reference");
    assertEquals(conversation.id,
        conversationId,
        "Check that the conversation has the correct id");

    final Message message = controller.newMessage(
        messageId,
        user.id,
        conversation.id,
        "Hello World",
        Time.now());

    assertNotNull(message, "Check that the message has a valid reference");
    assertEquals(message.id, messageId, "Check that the message has the correct id");
  }
}
