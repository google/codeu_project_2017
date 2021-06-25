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
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class BatchView extends ServerView {

  public BatchView(Model model) {
    super(model);
  }

  /**
   * Gets all users.
   */
  public Collection<User> getUsers() {
    return model().users().values();
  }

  /**
   * Gets all users whose id is found in |ids|.
   */
  public Collection<User> getUsers(Collection<UUID> ids) {
    var users = new ArrayList<User>(ids.size());

    for (var id : ids) {
      var user = model().users().get(id);
      if (user != null) {
        users.add(user);
      }
    }

    return users;
  }

  /**
   * Gets all conversations whose id is found in |ids|.
   */
  public Collection<Conversation> getConversations(Collection<UUID> ids) {
    var conversations = new ArrayList<Conversation>(ids.size());

    for (var id : ids) {
      var conversation = model().conversations().get(id);
      if (conversation != null) {
        conversations.add(conversation);
      }
    }

    return conversations;
  }

  /**
   * Gets all messages whose id is found in |ids|.
   */
  public Collection<Message> getMessages(Collection<UUID> ids) {
    var messages = new ArrayList<Message>(ids.size());

    for (var id : ids) {
      var message = model().messages().get(id);
      if (message != null) {
        messages.add(message);
      }
    }

    return messages;
  }
}
