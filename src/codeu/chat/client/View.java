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

import codeu.chat.common.SentimentScore;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import codeu.chat.common.BasicView;
import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.LogicalView;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

// VIEW
//
// This is the view component of the Model-View-Controller pattern used by the
// the client to reterive readonly data from the server. All methods are blocking
// calls.
public final class View implements BasicView, LogicalView {

  private final static Logger.Log LOG = Logger.newLog(View.class);

  private final BroadCastReceiver receiver;

  public View(BroadCastReceiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public Collection<User> getUsers(Collection<Uuid> ids) {

    final Collection<User> users = new ArrayList<>();

    final PrintWriter out = receiver.out();


    try {

      Serializers.INTEGER.write(out, NetworkCode.GET_USERS_BY_ID_REQUEST);
      Serializers.collection(Uuid.SERIALIZER).write(out, ids);

      if (receiver.getType() == NetworkCode.GET_USERS_BY_ID_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        users.addAll(Serializers.collection(User.SERIALIZER).read(in));
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return users;
  }

  @Override
  public Collection<ConversationSummary> getAllConversations() {

    final Collection<ConversationSummary> summaries = new ArrayList<>();

    final PrintWriter out = receiver.out();

    try  {

      Serializers.INTEGER.write(out, NetworkCode.GET_ALL_CONVERSATIONS_REQUEST);

      if (receiver.getType() == NetworkCode.GET_ALL_CONVERSATIONS_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        summaries.addAll(Serializers.collection(ConversationSummary.SERIALIZER).read(in));
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return summaries;
  }

  @Override
  public Collection<Conversation> getConversations(Collection<Uuid> ids) {

    final Collection<Conversation> conversations = new ArrayList<>();

    final PrintWriter out = receiver.out();

    try {

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_BY_ID_REQUEST);
      Serializers.collection(Uuid.SERIALIZER).write(out, ids);

      if (receiver.getType() == NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        conversations.addAll(Serializers.collection(Conversation.SERIALIZER).read(in));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return conversations;
  }

  @Override
  public Collection<Message> getMessages(Collection<Uuid> ids) {

    final Collection<Message> messages = new ArrayList<>();

    final PrintWriter out = receiver.out();

    try {

      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_ID_REQUEST);
      Serializers.collection(Uuid.SERIALIZER).write(out, ids);

      if (receiver.getType() == NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        messages.addAll(Serializers.collection(Message.SERIALIZER).read(in));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return messages;
  }

  @Override
  public Uuid getUserGeneration() {

    Uuid generation = Uuid.NULL;

    final PrintWriter out = receiver.out();

    try {

      Serializers.INTEGER.write(out, NetworkCode.GET_USER_GENERATION_REQUEST);

      if (receiver.getType() == NetworkCode.GET_USER_GENERATION_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        generation = Uuid.SERIALIZER.read(in);

      } else {
        LOG.error("Response from server failed");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return generation;
  }

  @Override
  public Collection<User> getUsersExcluding(Collection<Uuid> ids) {

    final Collection<User> users = new ArrayList<>();

    final PrintWriter out = receiver.out();


    try {

      Serializers.INTEGER.write(out, NetworkCode.GET_USERS_EXCLUDING_REQUEST);
      Serializers.collection(Uuid.SERIALIZER).write(out, ids);

      if (receiver.getType() == NetworkCode.GET_USERS_EXCLUDING_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        users.addAll(Serializers.collection(User.SERIALIZER).read(in));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return users;
  }

  @Override
  public Collection<Conversation> getConversations(Time start, Time end) {

    final Collection<Conversation> conversations = new ArrayList<>();

    final PrintWriter out = receiver.out();

    try {

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_BY_TIME_REQUEST);
      Time.SERIALIZER.write(out, start);
      Time.SERIALIZER.write(out, end);

      if (receiver.getType() == NetworkCode.GET_CONVERSATIONS_BY_TIME_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        conversations.addAll(Serializers.collection(Conversation.SERIALIZER).read(in));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return conversations;
  }

  @Override
  public Collection<Conversation> getConversations(String filter) {

    final Collection<Conversation> conversations = new ArrayList<>();

    final PrintWriter out = receiver.out();

    try  {

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_BY_TITLE_REQUEST);
      Serializers.STRING.write(out, filter);

      if (receiver.getType() == NetworkCode.GET_CONVERSATIONS_BY_TITLE_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        conversations.addAll(Serializers.collection(Conversation.SERIALIZER).read(in));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return conversations;
  }

  @Override
  public Collection<Message> getMessages(Uuid conversation, Time start, Time end) {

    final Collection<Message> messages = new ArrayList<>();

    final PrintWriter out = receiver.out();

    try {

      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_TIME_REQUEST);
      Time.SERIALIZER.write(out, start);
      Time.SERIALIZER.write(out, end);

      if (receiver.getType() == NetworkCode.GET_MESSAGES_BY_TIME_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        messages.addAll(Serializers.collection(Message.SERIALIZER).read(in));
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return messages;
  }

  @Override
  public Collection<Message> getMessages(Uuid rootMessage, int range) {

    final Collection<Message> messages = new ArrayList<>();

    final PrintWriter out = receiver.out();

    try {

      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_RANGE_REQUEST);
      Uuid.SERIALIZER.write(out, rootMessage);
      Serializers.INTEGER.write(out, range);

      if (receiver.getType() == NetworkCode.GET_MESSAGES_BY_RANGE_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        messages.addAll(Serializers.collection(Message.SERIALIZER).read(in));
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return messages;
  }

  public SentimentScore getSentimentScore(User user) {

    if (user == null) {
      throw new IllegalArgumentException();
    }

    final PrintWriter out = receiver.out();
    SentimentScore score = null;
    // todo (raami): correct after merging of realtime
    try {

      Serializers.INTEGER.write(out, NetworkCode.GET_USER_SCORE_REQUEST);
      User.SERIALIZER.write(out, user);

      if (receiver.getType() == NetworkCode.GET_USER_SCORE_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        score = SentimentScore.SERIALIZER.read(in);
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return score;
  }

}
