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

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.Key;

import codeu.chat.common.*;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

public class Controller implements BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);
  public RSA rsa = new RSA();
  private final ConnectionSource source;

  public Controller(ConnectionSource source) {
    this.source = source;
  }

  public Message newMessage(Uuid author, Uuid conversation, String body, EncryptionKey publicKey) {
    return processNewMessage(author, conversation, body, null, publicKey);
  }
  @Override
  public Message newMessage(Uuid author, Uuid conversation, String body) {
    return processNewMessage(author, conversation, body, null);
  }

  public Message newFileMessage(Uuid author, Uuid conversation, String body, File file, EncryptionKey publicKey){
    return processNewMessage(author, conversation, body, file, publicKey);
  }

  private Message processNewMessage(Uuid author, Uuid conversation, String body, File file, EncryptionKey publicKey){
    Message response = null;

    try (final Connection connection = source.connect()) {

      if (file != null){
        Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_FILE_MESSAGE_REQUEST);
      } else {
        Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_MESSAGE_REQUEST);
      }

      Uuid.SERIALIZER.write(connection.out(), author);
      Uuid.SERIALIZER.write(connection.out(), conversation);
      BigInteger encrypted = RSA.encrypt(RSA.messageToBigInteger(body), publicKey);
      System.out.println("Encrypted Message sent to Server " + encrypted);
      body = RSA.keyToString(encrypted);
      Serializers.STRING.write(connection.out(), body);

      if(file != null){
        Serializers.BYTES.write(connection.out(), Files.readAllBytes(file.toPath()));
      }

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.NEW_MESSAGE_RESPONSE) {
        response = Serializers.nullable(Message.SERIALIZER).read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      ex.printStackTrace();
      LOG.error(ex, "Exception during call on server.");
    }

    return response;

  }

  private Message processNewMessage(Uuid author, Uuid conversation, String body, File file){
    Message response = null;

    try (final Connection connection = source.connect()) {

      if (file != null){
        Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_FILE_MESSAGE_REQUEST);
      } else {
        Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_MESSAGE_REQUEST);
      }

      Uuid.SERIALIZER.write(connection.out(), author);
      Uuid.SERIALIZER.write(connection.out(), conversation);
      Serializers.STRING.write(connection.out(), body);

      if(file != null){
        Serializers.BYTES.write(connection.out(), Files.readAllBytes(file.toPath()));
      }

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

  public Conversation newConversation(String title, Uuid owner)  {

    Conversation response = null;

    RSA.generateKeys(1024);
    EncryptionKey publicKey = rsa.getPubKey();
    EncryptionKey secretKey = rsa.getSecKey();
    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.NEW_CONVERSATION_REQUEST);
      Serializers.STRING.write(connection.out(), title);
      Uuid.SERIALIZER.write(connection.out(), owner);
      Serializers.BIG_INTEGER.write(connection.out(), publicKey.getNumber());
      Serializers.BIG_INTEGER.write(connection.out(), secretKey.getNumber());
      Serializers.BIG_INTEGER.write(connection.out(), publicKey.getModulus());
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

  // Serialize the request for searchUserInDatabase with username and password parameters
  @Override
  public User searchUserInDatabase(String username, String password){
    User response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.SEARCH_USER_IN_DATABASE_REQUEST);
      Serializers.STRING.write(connection.out(), username);
      Serializers.STRING.write(connection.out(), password);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.SEARCH_USER_IN_DATABASE_RESPONSE) {
        response = Serializers.nullable(User.SERIALIZER).read(connection.in());
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
