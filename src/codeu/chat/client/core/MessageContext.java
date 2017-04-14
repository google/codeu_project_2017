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

import codeu.chat.common.BasicView;
import codeu.chat.common.Message;
import codeu.chat.util.Uuid;

public final class MessageContext {

  public final Message message;
  private final BasicView view;

  public MessageContext(Message message, BasicView view) {
    this.message = message;
    this.view = view;
  }

  public MessageContext next() {
    final Collection<Uuid> ids = Arrays.asList(message.next);
    for (final Message message : view.getMessages(ids)) {
      return new MessageContext(message, view);
    }
    return null;
  }

  public MessageContext previous() {
    final Collection<Uuid> ids = Arrays.asList(message.previous);
    for (final Message message : view.getMessages(ids)) {
      return new MessageContext(message, view);
    }
    return null;
  }
}
