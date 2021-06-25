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

import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.User;
import codeu.chat.util.Serializers;
import codeu.chat.util.Timeline;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.logging.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.UUID;

public final class Server {

  private final Timeline timeline = new Timeline();

  private final Model model = new Model();

  public void handleConnection(Connection connection) {
    timeline.submit(() -> {
      try {
        Log.instance.info("Handling connection...");

        onMessage(connection.in(), connection.out());

        Log.instance.info("Connection handled.");
      } catch (Exception ex) {
        Log.instance.error("Exception while handling connection: %s", ex.getMessage());
      }

      try {
        connection.close();
      } catch (Exception ex) {
        Log.instance.error("Exception while closing connection: %s", ex.getMessage());
      }
    });
  }

  private void onMessage(InputStream in, OutputStream out) throws IOException {

    final int type = Serializers.INTEGER.read(in);

    if (type == NetworkCode.NEW_MESSAGE_REQUEST) {

      final UUID author = Serializers.UUID.read(in);
      final UUID conversation = Serializers.UUID.read(in);
      final String content = Serializers.STRING.read(in);

      var controller = new CreationController(model);
      final Message message = controller.newMessage(author, conversation, content);

      Serializers.INTEGER.write(out, NetworkCode.NEW_MESSAGE_RESPONSE);
      Serializers.nullable(Message.SERIALIZER).write(out, message);

    } else if (type == NetworkCode.NEW_USER_REQUEST) {

      final String name = Serializers.STRING.read(in);

      var controller = new CreationController(model);
      final User user = controller.newUser(name);

      Serializers.INTEGER.write(out, NetworkCode.NEW_USER_RESPONSE);
      Serializers.nullable(User.SERIALIZER).write(out, user);
    } else if (type == NetworkCode.GET_USERS_BY_ID_REQUEST) {

      final Collection<UUID> ids = Serializers.collection(Serializers.UUID).read(in);

      var view = new BatchView(model);
      var users = view.getUsers(ids);

      Serializers.INTEGER.write(out, NetworkCode.GET_USERS_BY_ID_RESPONSE);
      Serializers.collection(User.SERIALIZER).write(out, users);

    } else if (type == NetworkCode.GET_MESSAGES_BY_ID_REQUEST) {

      final Collection<UUID> ids = Serializers.collection(Serializers.UUID).read(in);

      var view = new BatchView(model);
      var messages = view.getMessages(ids);

      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_ID_RESPONSE);
      Serializers.collection(Message.SERIALIZER).write(out, messages);

    } else if (type == NetworkCode.GET_ALL_USERS_REQUEST) {

      var view = new BatchView(model);
      var users = view.getUsers();

      Serializers.INTEGER.write(out, NetworkCode.GET_ALL_USERS_RESPONSE);
      Serializers.collection(User.SERIALIZER).write(out, users);

    } else if (type == NetworkCode.NEW_CONVERSATION_REQUEST) {

      final String title = Serializers.STRING.read(in);
      final UUID owner = Serializers.UUID.read(in);

      var controller = new CreationController(model);
      final Conversation conversation = controller.newConversation(title, owner);

      Serializers.INTEGER.write(out, NetworkCode.NEW_CONVERSATION_RESPONSE);
      Serializers.nullable(Conversation.SERIALIZER).write(out, conversation);

    } else if (type == NetworkCode.GET_ALL_CONVERSATIONS_REQUEST) {
      var view = new SummaryView(model);
      var conversations = view.getConversations();

      Serializers.INTEGER.write(out, NetworkCode.GET_ALL_CONVERSATIONS_RESPONSE);
      Serializers.collection(ConversationSummary.SERIALIZER).write(out, conversations);

    } else if (type == NetworkCode.GET_CONVERSATIONS_BY_ID_REQUEST) {

      final Collection<UUID> ids = Serializers.collection(Serializers.UUID).read(in);

      var view = new BatchView(model);
      var conversations = view.getConversations(ids);

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE);
      Serializers.collection(Conversation.SERIALIZER).write(out, conversations);

    } else {

      // In the case that the message was not handled make a dummy message with
      // the type "NO_MESSAGE" so that the client still gets something.

      Serializers.INTEGER.write(out, NetworkCode.NO_MESSAGE);

    }
  }
}
