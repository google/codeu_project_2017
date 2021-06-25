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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Model {

  private HashMap<UUID, User> mUsers = new HashMap<>();
  private HashMap<UUID, Conversation> mConversations = new HashMap<>();
  private HashMap<UUID, Message> mMessages = new HashMap<>();

  public Map<UUID, User> users() {
    return mUsers;
  }

  public Map<UUID, Conversation> conversations() {
    return mConversations;
  }

  public Map<UUID, Message> messages() {
    return mMessages;
  }
}
