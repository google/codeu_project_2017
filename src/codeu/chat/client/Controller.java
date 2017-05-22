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

import java.io.BufferedReader;
import java.io.PrintWriter;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Uuid;

public class Controller implements BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);

  private final BroadCastReceiver receiver;

  public Controller(BroadCastReceiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public Message newMessage(Uuid author, Uuid conversation, String body) {

    Message response = null;

    final PrintWriter out = receiver.out();
    try {
      Serializers.INTEGER.write(out, NetworkCode.NEW_MESSAGE_REQUEST);
      Uuid.SERIALIZER.write(out, author);
      Uuid.SERIALIZER.write(out, conversation);
      Serializers.STRING.write(out, body);

      if (receiver.getType() == NetworkCode.NEW_MESSAGE_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        response = Serializers.nullable(Message.SERIALIZER).read(in);
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return response;
  }

  @Override
  public User newUser(String name) {

    User response = null;

    final PrintWriter out = receiver.out();
    try {

      Serializers.INTEGER.write(out, NetworkCode.NEW_USER_REQUEST);
      Serializers.STRING.write(out, name);
      LOG.info("newUser: Request completed.");

      if (receiver.getType() == NetworkCode.NEW_USER_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        response = Serializers.nullable(User.SERIALIZER).read(in);

        LOG.info("newUser: Response completed.");
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return response;
  }

  @Override
  public Conversation newConversation(String title, Uuid owner)  {

    Conversation response = null;

    final PrintWriter out = receiver.out();

    try {

      Serializers.INTEGER.write(out, NetworkCode.NEW_CONVERSATION_REQUEST);
      Serializers.STRING.write(out, title);
      Uuid.SERIALIZER.write(out, owner);

      if (receiver.getType() == NetworkCode.NEW_CONVERSATION_RESPONSE) {
        BufferedReader in = receiver.getInputStream();
        response = Serializers.nullable(Conversation.SERIALIZER).read(in);
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    receiver.responseProcessed();
    return response;
  }
}
