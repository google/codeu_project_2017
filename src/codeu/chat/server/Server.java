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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.LinearUuidGenerator;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.Relay;
import codeu.chat.common.User;
import codeu.chat.server.model.Request;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Timeline;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public final class Server {

  private static final Logger.Log LOG = Logger.newLog(Server.class);

  private static final int RELAY_REFRESH_MS = 5000;  // 5 seconds

  private final Timeline timeline = new Timeline();

  private final Uuid id;
  private final byte[] secret;

  private final Model model = new Model();
  private final View view = new View(model);
  private final Controller controller;

  private final Relay relay;
  private Uuid lastSeen = Uuid.NULL;

  public Server(final Uuid id, final byte[] secret, final Relay relay) {

    this.id = id;
    this.secret = Arrays.copyOf(secret, secret.length);

    this.controller = new Controller(id, model);
    this.relay = relay;

    timeline.scheduleNow(new Runnable() {
      @Override
      public void run() {
        try {

          LOG.info("Reading update from relay...");

          for (final Relay.Bundle bundle : relay.read(id, secret, lastSeen, 32)) {
            onBundle(bundle);
            lastSeen = bundle.id();
          }

        } catch (Exception ex) {

          LOG.error(ex, "Failed to read update from relay.");

        }
        timeline.scheduleIn(RELAY_REFRESH_MS, this);
      }
    });
  }

  public void kill() {
    timeline.stop();
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

  /**
   *
   * Switch between serialized and restful modes. We use a neat trick here to do so.
   * Byte looking at the first byte of an incoming request, we can determine whether it is
   * restful or serial.
   *
   * Typically you'd want to do something like inserting a signal byte, but
   * because we want to connect relay we cannot rely on such a byte being present. Likewise,
   * we cannot rely on a restful client to append any byte before his request.
   *
   * However, since Network Codes are always less than 31, which are numbers with values that correspond to the
   * ASCII control characters, which will never lead a restful request. Therefore, we can simply check whether
   * this first byte is less than 31 to make a determination on how to process the incoming data.
   *
   * Create a PushbackInputStream so we can restore our input buffer after checking the lead byte.
   *
   * @param in input stream from remote.
   * @param out output stream to remote.
   * @return success
   * @throws IOException
   */
  private boolean onMessage(InputStream in, OutputStream out) throws IOException {
    PushbackInputStream pb = new PushbackInputStream(in);
    int leadByte = pb.read();
    pb.unread(leadByte);
    if (leadByte < NetworkCode.MAX_NETWORK_CODE) {
      return onSerialMessage(pb, out);
    } else {
      return onRestfulMessage(pb, out);
    }
  }

  private boolean onRestfulMessage(InputStream in, OutputStream out) throws IOException {
    LOG.info("Receiving a RESTful message.");
    Request r = RequestHandler.parseRaw(in);

    if (r.getHeader("type") == null) {
      return RequestHandler.failResponse(out, "Missing type header, which specifies which function to run.");
    }

    if (r.getVerb().equals("POST")) {

      switch (r.getHeader("type")) {

        // Creates a new message
        case ("NEW_MESSAGE_REQUEST"):
          final Uuid author = Uuid.parse(r.getHeader("author"));
          final Uuid conversation = Uuid.parse(r.getHeader("conversation"));
          final String content = r.getHeader("content");
          if (author == null || conversation == null || content == null) {
            return RequestHandler.failResponse(out, "Missing or invalid author, conversation, or content header.");
          }
          final Message message = controller.newMessage(author, conversation, content);
          if (message == null) {
            return RequestHandler.failResponse(out, "Invalid message.");
          }
          return RequestHandler.successResponse(out, message.toString());

        // Creates a new user
        case ("NEW_USER"):
          final String name = r.getBody();
          if (name == null) {
            return RequestHandler.failResponse(out, "Missing or invalid name header.");
          }
          final User user = controller.newUser(name);
          if (user == null) {
            return RequestHandler.failResponse(out, "Invalid username.");
          }
          return RequestHandler.successResponse(out, user.toString());

        // Creates a new conversation
        case ("NEW_CONVERSATION"):
          final String title = r.getBody();
          final Uuid owner = Uuid.parse(r.getHeader("owner"));
          if (title == null || owner == null) {
            return RequestHandler.failResponse(out, "Missing or invalid title or owner header.");
          }
          final Conversation conv = controller.newConversation(title, owner);
          if (conv == null) {
            return RequestHandler.failResponse(out, "Invalid conversation.");
          }
          return RequestHandler.successResponse(out, conv.id.toString());

        default:
          return RequestHandler.failResponse(out, "Unknown function type.");

      }

    } else if (r.getVerb().equals("GET")) {
      Collection<User> users;
      Collection<String> ids;
      Collection<Uuid> uuids;
      String value;
      Gson g = new Gson();
      Type listType = new TypeToken<Collection<String>>(){}.getType();

      switch (r.getHeader("type")) {

        // Returns a list of all users
        case ("ALL_USERS"):
          users = view.getUsersExcluding(new ArrayList<Uuid>());
          return RequestHandler.successResponse(out, users.toString());

        // Return list of all users in the provided list of UUIDs
        case ("GET_USERS"):
          value = null;
          String pos = null;
          try {
            value = r.getHeader("uuids");
            ids = g.fromJson(value, listType);
            uuids = new ArrayList<Uuid>();
            for (String item : ids) {
              pos = item;
              uuids.add(Uuid.parse(item));
            }
            users = view.getUsers(uuids);
            return RequestHandler.successResponse(out, users.toString());
          } catch (JsonSyntaxException e) {
            return RequestHandler.failResponse(out, "Malformed array in GET header (" + value + ").");
          } catch (NumberFormatException e) {
            return RequestHandler.failResponse(out, "Invalid UUID provided from uuid array: " + pos + ".");
          }


        default:
          return RequestHandler.failResponse(out, "Unknown function type.");
      }

    } else {
      return RequestHandler.failResponse(out, "Unknown HTTP verb.");
    }

  }

  private boolean onSerialMessage(InputStream in, OutputStream out) throws IOException {
    LOG.info("Receiving a serial message.");

    final int type = Serializers.INTEGER.read(in);

    if (type == NetworkCode.NEW_MESSAGE_REQUEST) {

      final Uuid author = Uuid.SERIALIZER.read(in);
      final Uuid conversation = Uuid.SERIALIZER.read(in);
      final String content = Serializers.STRING.read(in);

      final Message message = controller.newMessage(author, conversation, content);

      Serializers.INTEGER.write(out, NetworkCode.NEW_MESSAGE_RESPONSE);
      Serializers.nullable(Message.SERIALIZER).write(out, message);

      timeline.scheduleNow(createSendToRelayEvent(
          author,
          conversation,
          message.id));

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

  private void onBundle(Relay.Bundle bundle) {

    final Relay.Bundle.Component relayUser = bundle.user();
    final Relay.Bundle.Component relayConversation = bundle.conversation();
    final Relay.Bundle.Component relayMessage = bundle.user();

    User user = model.userById().first(relayUser.id());

    if (user == null) {
      user = controller.newUser(relayUser.id(), relayUser.text(), relayUser.time());
    }

    Conversation conversation = model.conversationById().first(relayConversation.id());

    if (conversation == null) {

      // As the relay does not tell us who made the conversation - the first person who
      // has a message in the conversation will get ownership over this server's copy
      // of the conversation.
      conversation = controller.newConversation(relayConversation.id(),
                                                relayConversation.text(),
                                                user.id,
                                                relayConversation.time());
    }

    Message message = model.messageById().first(relayMessage.id());

    if (message == null) {
      message = controller.newMessage(relayMessage.id(),
                                      user.id,
                                      conversation.id,
                                      relayMessage.text(),
                                      relayMessage.time());
    }
  }

  private Runnable createSendToRelayEvent(final Uuid userId,
                                          final Uuid conversationId,
                                          final Uuid messageId) {
    return new Runnable() {
      @Override
      public void run() {
        final User user = view.findUser(userId);
        final Conversation conversation = view.findConversation(conversationId);
        final Message message = view.findMessage(messageId);
        relay.write(id,
                    secret,
                    relay.pack(user.id, user.name, user.creation),
                    relay.pack(conversation.id, conversation.title, conversation.creation),
                    relay.pack(message.id, message.content, message.creation));
      }
    };
  }
}
