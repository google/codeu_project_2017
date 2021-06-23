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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class BasicControllerTest {

  private BasicController controller;

  @BeforeEach
  public void doBefore() {
    Model model = new Model();
    controller = new Controller(model);
  }

  @Test
  public void testAddUser() {

    final User user = controller.newUser("user");

    assertNotNull(user, "Check that user has a valid reference");
  }

  @Test
  public void testAddConversation() {

    final User user = controller.newUser("user");

    assertNotNull(user, "Check that user has a valid reference");

    final Conversation conversation = controller.newConversation(
        "conversation",
        user.id);

    assertNotNull(conversation, "Check that conversation has a valid reference");
  }

  @Test
  public void testAddMessage() {

    final User user = controller.newUser("user");

    assertNotNull(user, "Check that user has a valid reference");

    final Conversation conversation = controller.newConversation(
        "conversation",
        user.id);

    assertNotNull(conversation, "Check that conversation has a valid reference");

    final Message message = controller.newMessage(
        user.id,
        conversation.id,
        "Hello World");

    assertNotNull(message, "Check that the message has a valid reference");
  }
}
