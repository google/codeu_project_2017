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

package codeu.chat.client;

import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.User;
import codeu.chat.util.Serializers;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.logging.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

// VIEW
//
// This is the view component of the Model-View-Controller pattern used by the
// the client to retrieve readonly data from the server. All methods are blocking
// calls.
public final class View {

  private final ConnectionSource source;

  public View(ConnectionSource source) {
    this.source = source;
  }

  public Collection<User> getAllUsers() {
    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_ALL_USERS_REQUEST);

      if (Serializers.INTEGER.read(connection.in()) != NetworkCode.GET_ALL_USERS_RESPONSE) {
        Log.instance.error("Response from server failed.");
        return Collections.emptyList();
      }

      return Serializers.collection(User.SERIALIZER).read(connection.in());
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      Log.instance.error("Exception during call on server: %s", ex.getMessage());
    }

    return Collections.emptyList();
  }

  public Collection<User> getUsers(Collection<UUID> ids) {
    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_USERS_BY_ID_REQUEST);
      Serializers.collection(Serializers.UUID).write(connection.out(), ids);

      if (Serializers.INTEGER.read(connection.in()) != NetworkCode.GET_USERS_BY_ID_RESPONSE) {
        Log.instance.error("Response from server failed.");
        return Collections.emptyList();
      }

      return Serializers.collection(User.SERIALIZER).read(connection.in());
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      Log.instance.error("Exception during call on server: %s", ex.getMessage());
    }

    return Collections.emptyList();
  }

  public Collection<ConversationSummary> getAllConversations() {

    final Collection<ConversationSummary> summaries = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_ALL_CONVERSATIONS_REQUEST);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_ALL_CONVERSATIONS_RESPONSE) {
        summaries
            .addAll(Serializers.collection(ConversationSummary.SERIALIZER).read(connection.in()));
      } else {
        Log.instance.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      Log.instance.error("Exception during call on server: %s", ex.getMessage());
    }

    return summaries;
  }

  public Collection<Conversation> getConversations(Collection<UUID> ids) {

    final Collection<Conversation> conversations = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_CONVERSATIONS_BY_ID_REQUEST);
      Serializers.collection(Serializers.UUID).write(connection.out(), ids);

      if (Serializers.INTEGER.read(connection.in())
          == NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE) {
        conversations.addAll(Serializers.collection(Conversation.SERIALIZER).read(connection.in()));
      } else {
        Log.instance.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      Log.instance.error("Exception during call on server: %s", ex.getMessage());
    }

    return conversations;
  }

  public Collection<Message> getMessages(Collection<UUID> ids) {
    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_MESSAGES_BY_ID_REQUEST);
      Serializers.collection(Serializers.UUID).write(connection.out(), ids);

      if (Serializers.INTEGER.read(connection.in()) != NetworkCode.GET_MESSAGES_BY_ID_RESPONSE) {
        Log.instance.error("Response from server failed.");
        return Collections.emptyList();
      }

      return Serializers.collection(Message.SERIALIZER).read(connection.in());
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      Log.instance.error("Exception during call on server: %s", ex.getMessage());
    }

    return Collections.emptyList();
  }
}
