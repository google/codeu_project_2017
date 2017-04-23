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

import java.util.Collection;

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

// LOGICAL VIEW
//
//   The logical view is another view for the Model-View-Control pattern. This view
//   focuses on providing more logical methods of accessing data. Each function is
//   based on a query rather than fetching specific objects.
public interface LogicalView {

  // GET USER GENERATION
  //
  //   Get an identifier that specifies the generation of all users. Storing and
  //   tracking this number will allow checking if it is worth fetching all users.
  Uuid getUserGeneration();

  // GET USERS EXCLUDING
  //
  //   Get all users whose ID are not found in the given set of ids.
  Collection<User> getUsersExcluding(Collection<Uuid> ids);

  // GET CONVERSATIONS
  //
  //   Get a collection of conversations given the start and end of a time series.
  //   all conversations that are found to have been created between the start
  //   and end time will be returned.
  Collection<Conversation> getConversations(Time start, Time end);

  // GET CONVERSATIONS
  //
  //   Get a collection of conversations given a regex expression that will be
  //   used against every conversation's title. All conversations whose title
  //   matches the given regex expression will be returned.
  Collection<Conversation> getConversations(String filter);

  // GET MESSAGES
  //
  //   Get all messages from a single conversation whose time value falls
  //   between the start and end times. If the conversation is not found,
  //   or the start time is invalid, or the end time is invalid, no
  //   messages will be returned.
  Collection<Message> getMessages(Uuid conversation, Time start, Time end);

  // GET MESSAGES
  //
  //   Get all messages within the specified range starting from the given
  //   message id. If the range is greater than zero, all messages after the
  //   given messages up to and including |range| will be returned. If the
  //   range is negative, all messages before the given message up to and
  //   including |range| will be returned. If the root message is not found
  //   no messages will be returned.
  Collection<Message> getMessages(Uuid rootMessage, int range);

}
