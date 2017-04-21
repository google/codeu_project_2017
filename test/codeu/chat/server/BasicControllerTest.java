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

import java.sql.SQLException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;

import codeu.chat.database.Database;
import codeu.chat.server.database.UserSchema;
import codeu.chat.server.authentication.Authentication;
import codeu.chat.authentication.AuthenticationCode;

public final class BasicControllerTest {

  private Model model;
  private BasicController controller;

  private Database database;
  private UserSchema userSchema;
  private Authentication authentication;

  private User user;

  @Before
  public void doBefore() throws SQLException {
    // Setup the database.
    database = new Database("test.db");
    userSchema = new UserSchema();
    userSchema.dropTable("users", database);
    authentication = new Authentication(database);

    model = new Model();
    controller = new Controller(Uuid.NULL, model, authentication);
  }

  @Test
  public void testRegister() {

    int result = controller.newUser("username", "password");
    assertEquals(result, AuthenticationCode.SUCCESS);

  }

  @Test
  public void testLogin() {

    testRegister();
    user = controller.login("username", "password");
    assertNotNull(user);

  }

  @Test
  public void testAddConversation() {

    testLogin();

    assertFalse(
        "Check that user has a valid reference",
        user == null);

    final Conversation conversation = controller.newConversation(
        "conversation",
        user.id,
        user.token);

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);
  }

  @Test
  public void testAddMessage() {

    testLogin();

    assertFalse(
        "Check that user has a valid reference",
        user == null);

    final Conversation conversation = controller.newConversation(
        "conversation",
        user.id,
        user.token);

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);

    final Message message = controller.newMessage(
        user.id,
        user.token,
        conversation.id,
        "Hello World");

    assertFalse(
        "Check that the message has a valid reference",
        message == null);
  }
}
