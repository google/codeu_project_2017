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
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Timeline;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;

public final class Server {

  private static final Logger.Log LOG = Logger.newLog(Server.class);

  private final Timeline timeline = new Timeline();

  private final Uuid id;
  private final byte[] secret;

  private final Model model = new Model();
  private final View view = new View(model);
  private final Controller controller;

  private Uuid lastSeen = Uuid.NULL;

  public Server(final Uuid id, final byte[] secret) {

    this.id = id;
    this.secret = Arrays.copyOf(secret, secret.length);

    this.controller = new Controller(id, model);
  }

  public void handleConnection(final Connection connection) {
    timeline.scheduleNow(new Runnable() {
      @Override
      public void run() {
        try {

          LOG.info("Handling connection...");

          final boolean success = onMessage(
              connection.in(),
              connection.out());

          LOG.info("Connection handled: %s", success ? "ACCEPTED" : "REJECTED");
        } catch (Exception ex) {

          LOG.error(ex, "Exception while handling connection.");

        }

        try {
          connection.close();
        } catch (Exception ex) {
          LOG.error(ex, "Exception while closing connection.");
        }
      }
    });
  }

  private boolean onMessage(InputStream in, OutputStream out) throws IOException {

    final int type = Serializers.INTEGER.read(in);

    if (type == NetworkCode.NEW_MESSAGE_REQUEST) {

      final Uuid author = Uuid.SERIALIZER.read(in);
      final Uuid conversation = Uuid.SERIALIZER.read(in);
      final String content = Serializers.STRING.read(in);

      final Message message = controller.newMessage(author, conversation, content);

      Serializers.INTEGER.write(out, NetworkCode.NEW_MESSAGE_RESPONSE);
      Serializers.nullable(Message.SERIALIZER).write(out, message);

    } else if (type == NetworkCode.NEW_USER_REQUEST) {

      final String name = Serializers.STRING.read(in);

      final User user = controller.newUser(name);

      Serializers.INTEGER.write(out, NetworkCode.NEW_USER_RESPONSE);
      Serializers.nullable(User.SERIALIZER).write(out, user);

    } else if (type == NetworkCode.NEW_CONVERSATION_REQUEST) {

      final String title = Serializers.STRING.read(in);
      final Uuid owner = Uuid.SERIALIZER.read(in);

      final Conversation conversation = controller.newConversation(title, owner);

      Serializers.INTEGER.write(out, NetworkCode.NEW_CONVERSATION_RESPONSE);
      Serializers.nullable(Conversation.SERIALIZER).write(out, conversation);

    } else if (type == NetworkCode.GET_USERS_BY_ID_REQUEST) {

      final Collection<Uuid> ids = Serializers.collection(Uuid.SERIALIZER).read(in);

      final Collection<User> users = view.getUsers(ids);

      Serializers.INTEGER.write(out, NetworkCode.GET_USERS_BY_ID_RESPONSE);
      Serializers.collection(User.SERIALIZER).write(out, users);

    } else if (type == NetworkCode.GET_ALL_CONVERSATIONS_REQUEST) {

      final Collection<ConversationSummary> conversations = view.getAllConversations();

      Serializers.INTEGER.write(out, NetworkCode.GET_ALL_CONVERSATIONS_RESPONSE);
      Serializers.collection(ConversationSummary.SERIALIZER).write(out, conversations);

    } else if (type == NetworkCode.GET_CONVERSATIONS_BY_ID_REQUEST) {

      final Collection<Uuid> ids = Serializers.collection(Uuid.SERIALIZER).read(in);

      final Collection<Conversation> conversations = view.getConversations(ids);

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE);
      Serializers.collection(Conversation.SERIALIZER).write(out, conversations);

    } else if (type == NetworkCode.GET_MESSAGES_BY_ID_REQUEST) {

      final Collection<Uuid> ids = Serializers.collection(Uuid.SERIALIZER).read(in);

      final Collection<Message> messages = view.getMessages(ids);

      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_ID_RESPONSE);
      Serializers.collection(Message.SERIALIZER).write(out, messages);

    } else if (type == NetworkCode.GET_USER_GENERATION_REQUEST) {

      Serializers.INTEGER.write(out, NetworkCode.GET_USER_GENERATION_RESPONSE);
      Uuid.SERIALIZER.write(out, view.getUserGeneration());

    } else if (type == NetworkCode.GET_USERS_EXCLUDING_REQUEST) {

      final Collection<Uuid> ids = Serializers.collection(Uuid.SERIALIZER).read(in);

      final Collection<User> users = view.getUsersExcluding(ids);

      Serializers.INTEGER.write(out, NetworkCode.GET_USERS_EXCLUDING_RESPONSE);
      Serializers.collection(User.SERIALIZER).write(out, users);

    } else if (type == NetworkCode.GET_CONVERSATIONS_BY_TIME_REQUEST) {

      final Time startTime = Time.SERIALIZER.read(in);
      final Time endTime = Time.SERIALIZER.read(in);

      final Collection<Conversation> conversations = view.getConversations(startTime, endTime);

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_BY_TIME_RESPONSE);
      Serializers.collection(Conversation.SERIALIZER).write(out, conversations);

    } else if (type == NetworkCode.GET_CONVERSATIONS_BY_TITLE_REQUEST) {

      final String filter = Serializers.STRING.read(in);

      final Collection<Conversation> conversations = view.getConversations(filter);

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_BY_TITLE_RESPONSE);
      Serializers.collection(Conversation.SERIALIZER).write(out, conversations);

    } else if (type == NetworkCode.GET_MESSAGES_BY_TIME_REQUEST) {

      final Uuid conversation = Uuid.SERIALIZER.read(in);
      final Time startTime = Time.SERIALIZER.read(in);
      final Time endTime = Time.SERIALIZER.read(in);

      final Collection<Message> messages = view.getMessages(conversation, startTime, endTime);

      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_TIME_RESPONSE);
      Serializers.collection(Message.SERIALIZER).write(out, messages);

    } else if (type == NetworkCode.GET_MESSAGES_BY_RANGE_REQUEST) {

      final Uuid rootMessage = Uuid.SERIALIZER.read(in);
      final int range = Serializers.INTEGER.read(in);

      final Collection<Message> messages = view.getMessages(rootMessage, range);

      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_RANGE_RESPONSE);
      Serializers.collection(Message.SERIALIZER).write(out, messages);

    } else {

      // In the case that the message was not handled make a dummy message with
      // the type "NO_MESSAGE" so that the client still gets something.

      Serializers.INTEGER.write(out, NetworkCode.NO_MESSAGE);

    }

    return true;
  }
}
