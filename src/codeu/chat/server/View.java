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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import codeu.chat.common.BasicView;
import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.LogicalView;
import codeu.chat.common.Message;
import codeu.chat.common.SinglesView;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.StoreAccessor;

public final class View implements BasicView, LogicalView, SinglesView {

  private final static Logger.Log LOG = Logger.newLog(View.class);

  private final Model model;

  public View(Model model) {
    this.model = model;
  }


  @Override
  public Collection<User> getUsers(Collection<Uuid> ids) {
    return intersect(model.userById(), ids);
  }

  @Override
  public Collection<ConversationSummary> getAllConversations() {

    final Collection<ConversationSummary> summaries = new ArrayList<>();

    for (final Conversation conversation : model.conversationById().all()) {
        summaries.add(conversation.summary);
    }

    return summaries;

  }

  @Override
  public Collection<Conversation> getConversations(Collection<Uuid> ids) {
    return intersect(model.conversationById(), ids);
  }

  @Override
  public Collection<Message> getMessages(Collection<Uuid> ids) {
    return intersect(model.messageById(), ids);
  }

  @Override
  public Uuid getUserGeneration() {
    return model.userGeneration();
  }

  @Override
  public Collection<User> getUsersExcluding(Collection<Uuid> ids) {

    final Set<User> blacklist = new HashSet<>(intersect(model.userById(), ids));
    final Set<User> users = new HashSet<>();

    for (final User user : model.userById().all()) {
      if (!blacklist.contains(user)) {
        users.add(user);
      }
    }

    return users;
  }

  @Override
  public Collection<Conversation> getConversations(Time start, Time end) {

    final Collection<Conversation> conversations = new ArrayList<>();

    for (final Conversation conversation : model.conversationByTime().range(start, end)) {
      conversations.add(conversation);
    }

    return conversations;

  }

  @Override
  public Collection<Conversation> getConversations(String filter) {

    final Collection<Conversation> found = new ArrayList<>();

    for (final Conversation conversation : model.conversationByText().all()) {
      if (Pattern.matches(filter, conversation.title)) {
        found.add(conversation);
      }
    }

    return found;
  }

  @Override
  public Collection<Message> getMessages(Uuid conversation, Time start, Time end) {

    final Conversation foundConversation = model.conversationById().first(conversation);

    final List<Message> foundMessages = new ArrayList<>();

    Message current = (foundConversation == null) ?
        null :
        model.messageById().first(foundConversation.firstMessage);

    while (current != null && current.creation.compareTo(start) < 0) {
      current = model.messageById().first(current.next);
    }

    while (current != null && current.creation.compareTo(end) <= 0) {
      foundMessages.add(current);
      current = model.messageById().first(current.next);
    }

    return foundMessages;
  }

  @Override
  public Collection<Message> getMessages(Uuid rootMessage, int range) {

    int remaining = Math.abs(range);
    LOG.info("in getMessage: UUID=%s range=%d", rootMessage, range);

    // We want to return the messages in order. If the range was negative
    // the messages would be backwards. Use a linked list as it supports
    // adding at the front and adding at the end.

    final LinkedList<Message> found = new LinkedList<>();

    // i <= remaining : must be "<=" and not just "<" or else "range = 0" would
    // return nothing and we want it to return just the root because the description
    // is that the function will return "range" around the root. Zero messages
    // around the root means that it should just return the root.

    Message current = model.messageById().first(rootMessage);

    if (range > 0) {
      for (int i = 0; i <= remaining && current != null; i++) {
        found.addLast(current);
        current = model.messageById().first(current.next);
      }
    } else {
      for (int i = 0; i <= remaining && current != null; i++) {
        found.addFirst(current);
        current = model.messageById().first(current.previous);
      }
    }

    return found;
  }

  @Override
  public User findUser(Uuid id) { return model.userById().first(id); }

  @Override
  public Conversation findConversation(Uuid id) { return model.conversationById().first(id); }

  @Override
  public Message findMessage(Uuid id) { return model.messageById().first(id); }

  private static <T> Collection<T> intersect(StoreAccessor<Uuid, T> store, Collection<Uuid> ids) {

    // Use a set to hold the found users as this will prevent duplicate ids from
    // yielding duplicates in the result.

    final Collection<T> found = new HashSet<>();

    for (final Uuid id : ids) {

      final T t = store.first(id);

      if (t == null) {
        LOG.warning("Unmapped id %s", id);
      } else if (found.add(t)) {
        // do nothing
      } else {
        LOG.warning("Duplicate id %s", id);
      }
    }

    return found;
  }
}
