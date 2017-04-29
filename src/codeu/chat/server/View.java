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
import codeu.chat.common.ConversationHeader;
import codeu.chat.common.ConversationPayload;
import codeu.chat.common.Message;
import codeu.chat.common.SinglesView;
import codeu.chat.common.User;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.logging.ChatLog;
import codeu.logging.Logger;

public final class View implements BasicView, SinglesView {

  private static final Logger LOG = ChatLog.logger(View.class);

  private final Model model;

  public View(Model model) {
    this.model = model;
  }


  @Override
  public Collection<User> getUsers() {
    return new HashSet<User>(model.users.values());
  }

  @Override
  public Collection<ConversationHeader> getConversations() {
    return new HashSet<ConversationHeader>(model.headers.values());
  }

  @Override
  public Collection<ConversationPayload> getConversationPayloads(Collection<Uuid> ids) {
    return intersect(model.payloads, ids);
  }

  @Override
  public Collection<Message> getMessages(Collection<Uuid> ids) {
    return intersect(model.messages, ids);
  }

  @Override
  public User findUser(Uuid id) { return model.users.get(id); }

  @Override
  public ConversationHeader findConversation(Uuid id) { return model.headers.get(id); }

  @Override
  public Message findMessage(Uuid id) { return model.messages.get(id); }

  private static <T> Collection<T> intersect(Map<Uuid, T> map, Collection<Uuid> ids) {

    // Use a set to hold the found users as this will prevent duplicate ids from
    // yielding duplicates in the result.

    final Collection<T> found = new HashSet<>();

    for (final Uuid id : ids) {

      final T t = map.get(id);

      if (t == null) {
        LOG.warning("No value found for id=%s", id);
      } else if (found.add(t)) {
        // do nothing
      } else {
        LOG.warning("Duplicate value. The id %s was requested twice or two ids map to the same value.", id);
      }
    }

    return found;
  }
}
