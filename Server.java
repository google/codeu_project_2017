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
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;

import codeu.chat.common.*;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Timeline;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;

public final class Server {

  private static final Logger.Log LOG = Logger.newLog(Server.class);

  private static final int RELAY_REFRESH_MS = 5000;  // 5 seconds

  private final Timeline timeline = new Timeline();

  private final Uuid id;
  private final byte[] secret;

  private final Model model = new Model();
  private final View view = new View(model);
  private final Controller controller;

  private final RSA public_key;

  private final Relay relay;
  private Uuid lastSeen = Uuid.NULL;

  public Server(final Uuid id, final byte[] secret, final Relay relay) {

    this.id = id;
    this.secret = Arrays.copyOf(secret, secret.length);

    this.controller = new Controller(id, model);
    this.relay = relay;

    // generate public keys to use for clients
    BigInteger N = new BigInteger("121031328220179698619241735579399366066480633431913391889867690062393811868121480829220785581651508657516289567384667421886637451612229057349344668557877361518931322428842820919795475641300328608211927923636554712686783607321558272693627341254140596120513302416643381827584784317350259270991027163657220640873");
    BigInteger e = new BigInteger("3");
    BigInteger d = new BigInteger("80687552146786465746161157052932910710987088954608927926578460041595874578747653886147190387767672438344193044923111614591091634408152704899563112371918226098294772955986863499908818726240737779361618013202033539104628554389670993852009556060681625511216901384357325671976529751406322826292825490369300518659");
    public_key = new RSA(N, e, d);

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

      final int d_type = Serializers.INTEGER.read(in);

      // must decrypt message coming in from client
      final int type = Integer.parseInt(public_key.decrypt(Integer.toString(d_type)));

    if (type == NetworkCode.NEW_MESSAGE_REQUEST) {

      final Uuid author = Uuid.SERIALIZER.read(in);
      final Uuid conversation = Uuid.SERIALIZER.read(in);
      final String content = Serializers.STRING.read(in);

      final Message message = controller.newMessage(author, conversation, content);

      // encrypt NetworkCode before being sent out to client
      int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.NEW_MESSAGE_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Serializers.nullable(Message.SERIALIZER).write(out, message);

      timeline.scheduleNow(createSendToRelayEvent(
          author,
          conversation,
          message.id));

    } else if (type == NetworkCode.NEW_USER_REQUEST) {

      final String name = Serializers.STRING.read(in);

      final String hash = Serializers.STRING.read(in);

      final String salt = Serializers.STRING.read(in);

      final User user = controller.newUser(name, hash, salt);

      // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.NEW_USER_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
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

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_USERS_BY_ID_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Serializers.collection(User.SERIALIZER).write(out, users);

    } else if (type == NetworkCode.GET_ALL_CONVERSATIONS_REQUEST) {

      final Collection<ConversationSummary> conversations = view.getAllConversations();

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_ALL_CONVERSATIONS_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Serializers.collection(ConversationSummary.SERIALIZER).write(out, conversations);

    } else if (type == NetworkCode.GET_CONVERSATIONS_BY_ID_REQUEST) {

      final Collection<Uuid> ids = Serializers.collection(Uuid.SERIALIZER).read(in);

      final Collection<Conversation> conversations = view.getConversations(ids);

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Serializers.collection(Conversation.SERIALIZER).write(out, conversations);

    } else if (type == NetworkCode.GET_MESSAGES_BY_ID_REQUEST) {

      final Collection<Uuid> ids = Serializers.collection(Uuid.SERIALIZER).read(in);

      final Collection<Message> messages = view.getMessages(ids);

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_MESSAGES_BY_ID_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Serializers.collection(Message.SERIALIZER).write(out, messages);

    } else if (type == NetworkCode.GET_USER_GENERATION_REQUEST) {

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_USER_GENERATION_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Uuid.SERIALIZER.write(out, view.getUserGeneration());

    } else if (type == NetworkCode.GET_USERS_EXCLUDING_REQUEST) {

      final Collection<Uuid> ids = Serializers.collection(Uuid.SERIALIZER).read(in);

      final Collection<User> users = view.getUsersExcluding(ids);

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_USERS_EXCLUDING_RESPONSE)));

      Serializers.INTEGER.write(out, NetworkCode.GET_USERS_EXCLUDING_RESPONSE);
      Serializers.collection(User.SERIALIZER).write(out, users);

    } else if (type == NetworkCode.GET_CONVERSATIONS_BY_TIME_REQUEST) {

      final Time startTime = Time.SERIALIZER.read(in);
      final Time endTime = Time.SERIALIZER.read(in);

      final Collection<Conversation> conversations = view.getConversations(startTime, endTime);

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_CONVERSATIONS_BY_TIME_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Serializers.collection(Conversation.SERIALIZER).write(out, conversations);

    } else if (type == NetworkCode.GET_CONVERSATIONS_BY_TITLE_REQUEST) {

      final String filter = Serializers.STRING.read(in);

      final Collection<Conversation> conversations = view.getConversations(filter);

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_CONVERSATIONS_BY_TITLE_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Serializers.collection(Conversation.SERIALIZER).write(out, conversations);

    } else if (type == NetworkCode.GET_MESSAGES_BY_TIME_REQUEST) {

      final Uuid conversation = Uuid.SERIALIZER.read(in);
      final Time startTime = Time.SERIALIZER.read(in);
      final Time endTime = Time.SERIALIZER.read(in);

      final Collection<Message> messages = view.getMessages(conversation, startTime, endTime);

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_MESSAGES_BY_TIME_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Serializers.collection(Message.SERIALIZER).write(out, messages);

    } else if (type == NetworkCode.GET_MESSAGES_BY_RANGE_REQUEST) {

      final Uuid rootMessage = Uuid.SERIALIZER.read(in);
      final int range = Serializers.INTEGER.read(in);

      final Collection<Message> messages = view.getMessages(rootMessage, range);

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.GET_MESSAGES_BY_RANGE_RESPONSE)));

      Serializers.INTEGER.write(out, e_type);
      Serializers.collection(Message.SERIALIZER).write(out, messages);

    } else {

      // In the case that the message was not handled make a dummy message with
      // the type "NO_MESSAGE" so that the client still gets something.

        // encrypt NetworkCode before being sent out to client
        int e_type = Integer.parseInt(public_key.decrypt(Integer.toString(NetworkCode.NO_MESSAGE)));

      Serializers.INTEGER.write(out, e_type);

    }

    return true;
  }

  private void onBundle(Relay.Bundle bundle) {

    final Relay.Bundle.Component relayUser = bundle.user();
    final Relay.Bundle.Component relayConversation = bundle.conversation();
    final Relay.Bundle.Component relayMessage = bundle.user();

    User user = model.userById().first(relayUser.id());

    if (user == null) {
      user = controller.newUser(relayUser.id(), relayUser.text(), relayUser.time(), "hash problem", "salt problem");
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

  private RSA getPublic_key() { return public_key;}

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
