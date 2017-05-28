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
import codeu.chat.client.ClientUser;
import codeu.chat.client.View;
import codeu.chat.server.Controller;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.common.Uuids;
import codeu.chat.common.Uuid;
import codeu.chat.common.LinearUuidGenerator;

public final class DeleteUserTest {

  private Model model;
  private Controller controller;
  private Uuid.Generator UuidGenerator;

  @Before
  public void doBefore() {
    model = new Model();
    controller = new Controller(Uuids.NULL, model);
    UuidGenerator = new LinearUuidGenerator(null, 1, Integer.MAX_VALUE);
  }

  @Test
  public void testdeleteUserByText() {
	  final User user = controller.newUser("user");
	  
	  controller.deleteUser("user");
	  
	  assertNull(model.userByText().first("user"));
  }
  
  @Test
  public void testdeleteUserById() {
	  final User user = controller.newUser("user");
	  
	  controller.deleteUser("user");
	  
	  assertNull(model.userById().first(user.id));
  }
  
  @Test
  public void testdeleteUserByTime() {
	  final User user = controller.newUser("user");
	  
	  controller.deleteUser("user");
	  
	  assertNull(model.userByTime().first(user.creation));
  }
  
  @Test
  public void testdeleteMultipleUsers() {
	  final User testUser1 = controller.newUser("testUser1");
	  final User testUser2 = controller.newUser("testUser2");
	  
	  controller.deleteUser("testUser1");
	  controller.deleteUser("testUser2");
	  
	  assertTrue(model.userById().first(testUser1.id) == null&& model.userById().first(testUser2.id)==null);
  }
  
  @Test
  public void testNewConversationDeleteUser() {
	  final User user = controller.newUser("user");
	  Uuid testConversationUuid = UuidGenerator.make();
	  
	  controller.deleteUser("user");
	  
	  assertNull(controller.newConversation(testConversationUuid, "test", user.id, user.creation));
  }
  
}