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

package codeu.chat.common;

import codeu.chat.common.ConversationHeader;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;

// SINGLES VIEW
//
// A view as part of the Model-View-Controller pattern. This view is
// responsible for allowing single value reading from the model.
public interface SinglesView {

  // FIND USER
  //
  // Find the user whose id matches the given id. If no user's id matches
  // the given id, null with be returned.
  User findUser(Uuid id);

  // FIND CONVERSATION
  //
  // Find the conversation whose id matches the given id. If no conversation's
  // matches the given id, null will be returned.
  ConversationHeader findConversation(Uuid id);

  // FIND MESSAGE
  //
  // Find the message whose id matches the given id. if no message's id
  // matches the given id, null will be returned.
  Message findMessage(Uuid id);

}
