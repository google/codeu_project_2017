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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread; 

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

public class Controller implements BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);

  private final ConnectionSource source;

  public Controller(ConnectionSource source) {
    this.source = source;
  }

  @Override
  public Message newMessage(Uuid author, Uuid conversation, String body) {

    Message response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_MESSAGE_REQUEST);
      Uuid.SERIALIZER.write(connection.out(), author);
      Uuid.SERIALIZER.write(connection.out(), conversation);
      Serializers.STRING.write(connection.out(), body);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_MESSAGE_RESPONSE) {
        response = Serializers.nullable(Message.SERIALIZER).read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return response;
  }

  @Override
  public User newUser(String name) {

    User response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_USER_REQUEST);
      Serializers.STRING.write(connection.out(), name);
      LOG.info("newUser: Request completed.");

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_USER_RESPONSE) {
        response = Serializers.nullable(User.SERIALIZER).read(connection.in());
        LOG.info("newUser: Response completed.");
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return response;

  }

  public User newUser(String name, String password){

    System.out.println("password method");
    User response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_USER_REQUEST);
      Serializers.STRING.write(connection.out(), name);
      Serializers.STRING.write(connection.out(), password);

      LOG.info("newUser: Request completed.");

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_USER_RESPONSE) {
        response = Serializers.nullable(User.SERIALIZER).read(connection.in());
        LOG.info("newUser: Response completed.");
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    System.out.println("response: " + response);

    return response;

  }



  /*
 * Tells the server to delete the provided user
 *
 * Given a user to delete, serializes user information to the server and requests the
 * user be deleted from the server. Prints an error message if user is not
 * successfully deleted, and returns a boolean stating whether user was
 * successfully deleted.
 *
 * @param userToDelete user to be deleted from the server
 * @return boolean stating whether user was deleted from server
 */
  public boolean deleteUser(User userToDelete) {  
      
    boolean userDeleted = false; 

    try (final Connection connection = source.connect()) {
	  
      Serializers.INTEGER.write(connection.out(), NetworkCode.DELETE_USERS_REQUEST);
      User.SERIALIZER.write(connection.out(), userToDelete);

      // prints error message if user not successfully deleted
      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.DELETE_USERS_RESPONSE) {
        userDeleted = Serializers.BOOLEAN.read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server."); 
    }

    //boolean stating whether user was successfully deleted
    return userDeleted; 
  }

  public boolean addConversationUser(User user, Conversation conv){

    boolean userAdded = false;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.ADD_CONVERSATION_USER_REQUEST);
      Serializers.nullable(User.SERIALIZER).write(connection.out(), user);
      Serializers.nullable(Conversation.SERIALIZER).write(connection.out(), conv);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.ADD_CONVERSATION_USER_RESPONSE) { //read in response
        userAdded = Serializers.BOOLEAN.read(connection.in()); //read in boolean
        System.out.println("Boolean returned to ClientController [client/controller]: " + userAdded);
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return userAdded;

  }

  @Override
  public Conversation newConversation(String title, Uuid owner)  {

    Conversation response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_CONVERSATION_REQUEST);
      Serializers.STRING.write(connection.out(), title);
      Uuid.SERIALIZER.write(connection.out(), owner);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_CONVERSATION_RESPONSE) {
        response = Serializers.nullable(Conversation.SERIALIZER).read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return response;
  }
}
