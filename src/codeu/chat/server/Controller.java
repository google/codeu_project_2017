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

import java.util.Collection;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import com.sun.prism.shader.Solid_Color_AlphaTest_Loader;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public final class Controller implements RawController, BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);

  private final Model model;
  private final Uuid.Generator uuidGenerator;

  public Controller(Uuid serverId, Model model) {
    this.model = model;
    this.uuidGenerator = new RandomUuidGenerator(serverId, System.currentTimeMillis());
  }

  @Override
  public Message newMessage(Uuid author, Uuid conversation, String body) {
    return newMessage(createId(), author, conversation, body, Time.now());
  }

  @Override
  public User newUser(String name, String password) {
    return newUser(createId(), name, Time.now(), password);
  }

  @Override
  public Conversation newConversation(String title, Uuid owner) {
    return newConversation(createId(), title, owner, Time.now());
  }

  @Override
  public Message newMessage(Uuid id, Uuid author, Uuid conversation, String body, Time creationTime) {

    final User foundUser = model.userById().first(author);
    final Conversation foundConversation = model.conversationById().first(conversation);

    Message message = null;
    Connection connection = null;
    Statement stmt = null;

    if(sqlValidConversation(author, conversation)){
      try{
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
        connection.setAutoCommit(false);

        stmt = connection.createStatement();

        String sql = "INSERT INTO MESSAGES(ID, USERID, CONVERSATIONID, TimeCreated, MESSAGE)" +
                "VALUES("+sqlID(id)+","+sqlID(author)+","+sqlID(conversation)+","+sqlBody(body)+","+sqlCreationTime(creationTime)+");";
        stmt.executeUpdate(sql);

        stmt.close();
        connection.commit();
        connection.close();
      }catch (Exception e) {
        System.out.println("Error adding message to conversation");
        System.err.println(e.getClass().getName() +": " + e.getMessage());
        System.exit(0);
      }
    }

    if (foundUser != null && foundConversation != null && isIdFree(id)) {

      message = new Message(id, Uuid.NULL, Uuid.NULL, creationTime, author, body);
      model.add(message);
      LOG.info("Message added: %s", message.id);

      // Find and update the previous "last" message so that it's "next" value
      // will point to the new message.

      if (Uuid.equals(foundConversation.lastMessage, Uuid.NULL)) {

        // The conversation has no messages in it, that's why the last message is NULL (the first
        // message should be NULL too. Since there is no last message, then it is not possible
        // to update the last message's "next" value.

      } else {
        final Message lastMessage = model.messageById().first(foundConversation.lastMessage);
        lastMessage.next = message.id;
      }

      // If the first message points to NULL it means that the conversation was empty and that
      // the first message should be set to the new message. Otherwise the message should
      // not change.

      foundConversation.firstMessage =
          Uuid.equals(foundConversation.firstMessage, Uuid.NULL) ?
          message.id :
          foundConversation.firstMessage;

      // Update the conversation to point to the new last message as it has changed.

      foundConversation.lastMessage = message.id;

      if (!foundConversation.users.contains(foundUser)) {
        foundConversation.users.add(foundUser.id);
      }
    }

    return message;
  }

  @Override
  public User newUser(Uuid id, String name, Time creationTime, String password) {

    Connection connection = null;
    Statement stmt = null;
    User user = null;


    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
      connection.setAutoCommit(false);
      user = new User(id, name, creationTime, password);
      stmt = connection.createStatement();
      String sql = "INSERT INTO USERS (ID,UNAME,TIMECREATED,PASSWORD) " +
              "VALUES ("+sqlID(id)+", "+sqlName(name)+", "+sqlCreationTime(creationTime)+", "+sqlPassword(password)+");";
      stmt.executeUpdate(sql);

      LOG.info(
              "newUser success (user.id=%s user.name=%s user.time=%s)",
              id,
              name,
              creationTime);

      stmt.close();
      connection.commit();
      connection.close();
    } catch ( Exception e ) {
      LOG.info(
              "newUser fail - Database insertion error (user.id=%s user.name=%s user.time=%s)",
              id,
              name,
              creationTime);
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }

    if (isIdFree(id)) {

      user = new User(id, name, creationTime, password);
      model.add(user);

      LOG.info(
          "newUser success (user.id=%s user.name=%s user.time=%s)",
          id,
          name,
          creationTime);

    } else {

      LOG.info(
          "newUser fail - id in use (user.id=%s user.name=%s user.time=%s)",
          id,
          name,
          creationTime);
    }

    return user;
  }

  @Override
  public Conversation newConversation(Uuid id, String title, Uuid owner, Time creationTime) {

    final User foundOwner = model.userById().first(owner);
    Conversation conversation = null;
    Connection connection = null;
    Statement stmt = null;

    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
      connection.setAutoCommit(false);
      stmt = connection.createStatement();
      String sql = "INSERT INTO CONVERSATIONS (ID,CNAME,OWNERID,TimeCreated) " +
              "VALUES ("+sqlID(id)+", "+sqlName(title)+", "+sqlID(owner)+", "+sqlCreationTime(creationTime)+");";
      stmt.executeUpdate(sql);

      conversation = new Conversation(id, owner, creationTime, title);

      LOG.info("Conversation added: " + conversation.id);

      stmt.close();
      connection.commit();
      connection.close();
    } catch ( Exception e ) {
      LOG.info(
              "newConversation fail - Verify connection and try again shortly");
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }

    if (foundOwner != null && isIdFree(id)) {
      conversation = new Conversation(id, owner, creationTime, title);
      model.add(conversation);

      LOG.info("Conversation added: " + conversation.id);
    }

    return conversation;
  }

  private Uuid createId() {

    Uuid candidate;

    for (candidate = uuidGenerator.make();
         isIdInUse(candidate);
         candidate = uuidGenerator.make()) {

     // Assuming that "randomUuid" is actually well implemented, this
     // loop should never be needed, but just incase make sure that the
     // Uuid is not actually in use before returning it.

    }

    return candidate;
  }

  private String sqlID(Uuid userID){
    String sqlID = userID.toString();
    sqlID = sqlID.replace("[UUID:","");
    sqlID = sqlID.replace("]","");
    sqlID = "'" + sqlID + "'";
    return sqlID;
  }

  private String sqlName(String userName){
    String sqlName = "'" + userName + "'";
    return sqlName;
  }

  private String sqlCreationTime(Time userTime){
    SimpleDateFormat sqlFormatter = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
    String sqlCreationTime = sqlFormatter.format(new Date(userTime.inMs())).toString();
    sqlCreationTime = "'" + sqlCreationTime + "'";
    return sqlCreationTime;
  }

  private String sqlPassword(String userPassword){
    String sqlPassword = "'" + userPassword + "'";
    return sqlPassword;
  }

  private String sqlBody(String userBody){
    String sqlBody = "'" + userBody + "'";
    return sqlBody;
  }

  private boolean sqlValidConversation(Uuid userID, Uuid conversationID){
    boolean validConversation = false;

    Connection connection = null;
    Statement stmt = null;

    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
      connection.setAutoCommit(false);

      stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * " +
              "FROM USER_CONVERSATION" +
              "WHERE  USERID = "+sqlID(userID)+"" +
              "AND    CONVERSATIONID = "+sqlID(conversationID)+";" );
      if(rs.next()){
        validConversation = true;
        System.out.println("Conversation exists and User is a member");
      }
      rs.close();
      stmt.close();
      connection.close();
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }

    return validConversation;
  }

  private boolean isIdInUse(Uuid id) {
    return model.messageById().first(id) != null ||
           model.conversationById().first(id) != null ||
           model.userById().first(id) != null;
  }

  private boolean isIdFree(Uuid id) { return !isIdInUse(id); }

}
