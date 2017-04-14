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

package codeu.chat.client.core;

import java.util.Arrays;
import java.util.Collection;

import codeu.chat.common.BasicController;
import codeu.chat.common.BasicView;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;

public final class ConversationContext {

  public final User user;
  public final Conversation conversation;

  private final BasicView view;
  private final BasicController controller;

  public ConversationContext(User user,
                             Conversation conversation,
                             BasicView view,
                             BasicController controller) {

    this.user = user;
    this.conversation = conversation;
    this.view = view;
    this.controller = controller;
  }

  public MessageContext add(String messageBody) {

    final Message message = controller.newMessage(user.id,
                                                  conversation.id,
                                                  messageBody);

    return message == null ?
        null :
        new MessageContext(message, view);
  }

  public MessageContext firstMessage() {

    // As it is possible for the conversation to have been updated, so fetch
    // a new copy.
    final Conversation updated = getUpdated();

    if (updated == null) {
      return null;
    }

    final Collection<Uuid> ids = Arrays.asList(updated.firstMessage);
    for (final Message message : view.getMessages(ids)) {
      return new MessageContext(message, view);
    }

    return null;
  }

  public MessageContext lastMessage() {

    // As it is possible for the conversation to have been updated, so fetch
    // a new copy.
    final Conversation updated = getUpdated();

    final Collection<Uuid> ids = Arrays.asList(updated.lastMessage);
    for (final Message message : view.getMessages(ids)) {
      return new MessageContext(message, view);
    }

    return null;
  }

  private Conversation getUpdated() {

    for (final Conversation updated : view.getConversations(Arrays.asList(conversation.id))) {
      return updated;
    }

    return null;
  }
}
