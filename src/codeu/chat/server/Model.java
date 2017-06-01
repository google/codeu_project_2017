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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.LinearUuidGenerator;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.Store;
import codeu.chat.util.store.StoreAccessor;

import com.google.firebase.database.*;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

public final class Model {

  private static final Comparator<Uuid> UUID_COMPARE = new Comparator<Uuid>() {

    @Override
    public int compare(Uuid a, Uuid b) {

      if (a == b) { return 0; }

      if (a == null && b != null) { return -1; }

      if (a != null && b == null) { return 1; }

      final int order = Integer.compare(a.id(), b.id());
      return order == 0 ? compare(a.root(), b.root()) : order;
    }
  };

  private static final Comparator<Time> TIME_COMPARE = new Comparator<Time>() {
    @Override
    public int compare(Time a, Time b) {
      return a.compareTo(b);
    }
  };

  private static final Comparator<String> STRING_COMPARE = String.CASE_INSENSITIVE_ORDER;

  private final Store<Uuid, User> userById = new Store<>(UUID_COMPARE);
  private final Store<Time, User> userByTime = new Store<>(TIME_COMPARE);
  private final Store<String, User> userByText = new Store<>(STRING_COMPARE);

  private final Store<Uuid, Conversation> conversationById = new Store<>(UUID_COMPARE);
  private final Store<Time, Conversation> conversationByTime = new Store<>(TIME_COMPARE);
  private final Store<String, Conversation> conversationByText = new Store<>(STRING_COMPARE);

  private final Store<Uuid, Message> messageById = new Store<>(UUID_COMPARE);
  private final Store<Time, Message> messageByTime = new Store<>(TIME_COMPARE);
  private final Store<String, Message> messageByText = new Store<>(STRING_COMPARE);

  private final Uuid.Generator userGenerations = new LinearUuidGenerator(null, 1, Integer.MAX_VALUE);
  private Uuid currentUserGeneration = userGenerations.make();

  private final String USERS = "users";
  private final String MESSAGES = "messages";
  private final String CONVERSATIONS = "conversations";

  private static DatabaseReference database = FirebaseDatabase.getInstance().getReference();

  public void add(User user) {
    currentUserGeneration = userGenerations.make();

    userById.insert(user.id, user);
    userByTime.insert(user.creation, user);
    userByText.insert(user.name, user);

    DatabaseReference usersRef = database.child(USERS);

    Map<String, Object> userMap  = new HashMap<>();
    userMap.put(user.id.toString() + "/" + "name", user.name.toString());
    userMap.put(user.id.toString() + "/" + "creation", user.creation.toString());

    usersRef.updateChildren(userMap);
  }

  public StoreAccessor<Uuid, User> userById() {
    return userById;
  }

  public StoreAccessor<Time, User> userByTime() {
    return userByTime;
  }

  public StoreAccessor<String, User> userByText() {
    return userByText;
  }

  public Uuid userGeneration() {
    return currentUserGeneration;
  }

  public void add(Conversation conversation) {
    conversationById.insert(conversation.id, conversation);
    conversationByTime.insert(conversation.creation, conversation);
    conversationByText.insert(conversation.title, conversation);

    DatabaseReference convosRef = database.child(CONVERSATIONS);

    Map<String, Object> convosMap = new HashMap<>();
    convosMap.put(conversation.id.toString() + "/title", conversation.title);
    convosMap.put(conversation.id.toString() + "/creation", conversation.creation.toString());
    convosMap.put(conversation.id.toString() + "/owner" , conversation.owner.toString());

    convosRef.updateChildren(convosMap);
  }

  public StoreAccessor<Uuid, Conversation> conversationById() {
    return conversationById;
  }

  public StoreAccessor<Time, Conversation> conversationByTime() {
    return conversationByTime;
  }

  public StoreAccessor<String, Conversation> conversationByText() {
    return conversationByText;
  }

  public void add(Message message) {
    messageById.insert(message.id, message);
    messageByTime.insert(message.creation, message);
    messageByText.insert(message.content, message);

    DatabaseReference messagesRef = database.child(MESSAGES);

    Map<String, Object> messagesMap = new HashMap<>();
    messagesMap.put(message.id.toString() + "/creation", message.creation.toString());
    messagesMap.put(message.id.toString() + "/content", message.content.toString());
    messagesMap.put(message.id.toString() + "/author", message.author.toString());

    messagesRef.updateChildren(messagesMap);
  }

  public StoreAccessor<Uuid, Message> messageById() {
    return messageById;
  }

  public StoreAccessor<Time, Message> messageByTime() {
    return messageByTime;
  }

  public StoreAccessor<String, Message> messageByText() {
    return messageByText;
  }
}
