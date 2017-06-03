package codeu.chat.util.store;

import java.sql.*;
import java.util.*;
import codeu.chat.common.User;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.Time;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;

public class Database {
  Connection conn;
  Statement  stmt;
  PreparedStatement pstmt;

  public Database() {
    try {
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection("jdbc:sqlite:backup.db");
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }

  public void initialize() {
    try {
      stmt = conn.createStatement();
      String createUser = "CREATE TABLE IF NOT EXISTS User "
                        + "(id       STRING PRIMARY KEY NOT NULL,"
                        + " name     TEXT   NOT NULL,"
                        + " creation BIGINT NOT NULL)";

      String createConversation = "CREATE TABLE IF NOT EXISTS Conversation"
                                + "(id       STRING PRIMARY KEY NOT NULL,"
                                + " owner_id STRING NOT NULL,"
                                + " creation BIGINT NOT NULL,"
                                + " title    TEXT   NOT NULL,"
                                + " FOREIGN KEY(owner_id) REFERENCES User(id))";

      String createMessage = "CREATE TABLE IF NOT EXISTS Message"
                          + "(id STRING PRIMARY KEY  NOT NULL,"
                          + " conversation_id STRING NOT NULL,"
                          + " author_id       STRING NOT NULL,"
                          + " creation        BIGINT NOT NULL,"
                          + " content         TEXT   NOT NULL,"
                          + " FOREIGN KEY(conversation_id) REFERENCES Conversation(id),"
                          + " FOREIRN KEY(author_id) REFERENCES User(id))";

      stmt.executeUpdate(createUser);
      stmt.executeUpdate(createConversation);
      stmt.executeUpdate(createMessage);

      stmt.close();
    } catch(SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }

  public void addUser(User u) {
    try {
      pstmt = conn.prepareStatement("INSERT INTO User VALUES(?, ?, ?)");

      pstmt.setString(1, u.id.toString());
      pstmt.setString(2, u.name);
      pstmt.setLong(3, u.creation.inMs());
      pstmt.executeUpdate();
      pstmt.close();
      conn.commit();
    } catch(SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }

  public void addConversation(Conversation c) {
    try {
      pstmt = conn.prepareStatement("INSERT INTO Conversation VALUES(?, ?, ?, ?)");

      pstmt.setString(1, c.id.toString());
      pstmt.setString(2, c.owner.toString());
      pstmt.setLong(3, c.creation.inMs());
      pstmt.setString(4, c.title);
      pstmt.executeUpdate();
      pstmt.close();
      conn.commit();
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }

  public void addMessage(Message m) {
    try {
      stmt = conn.prepareStatement("INSERT INTO Message VALUES(?, ?, ?, ?)");

      pstmt.setString(1, m.id.toString());
      pstmt.setString(2, null); // set to null because not used and out of time to refactor
      pstmt.setString(3, m.author.toString());
      pstmt.setLong(4, m.creation.inMs());
      pstmt.setString(5, m.content);

      pstmt.executeUpdate();
      pstmt.close();
      conn.commit();
    } catch(SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }

  public ResultSet fetchMessages() {
    try {
      pstmt = conn.prepareStatement("SELECT * FROM Messages");
      ResultSet rs = pstmt.executeQuery();
      pstmt.close();
      return rs;
    } catch(SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
      return null;
    }
  }

  public ResultSet fetchConversations() {
    try {
      stmt = conn.createStatement();
      String sql = "SELECT * "
                 + "FROM Conversations ";
      ResultSet rs = stmt.executeQuery(sql);
      stmt.close();
      return rs;
    } catch(SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
      return null;
    }
  }

  public ResultSet fetchUsers() {
    try {
      stmt = conn.createStatement();
      String sql = "SELECT * "
                 + "FROM Users ";
      ResultSet rs = stmt.executeQuery(sql);
      stmt.close();
      return rs;
    } catch(SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
      return null;
    }
  }

  public ArrayList<User> restoreUsers() {
    try {
      ResultSet usersSet = fetchUsers();
      ArrayList<User> users = new ArrayList<User>();
      while(usersSet.next()) {
        User temp = new User(Uuids.fromString(usersSet.getString(1)), usersSet.getString(2), Time.fromMs(usersSet.getInt(3)));
        users.add(temp);
      }
      return users;
    } catch(SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
      return null;
    }
  }

  public ArrayList<Conversation> restoreConversations() {
    try {
      ResultSet conversationsSet = fetchConversations();
      ArrayList<Conversation> conversations = new ArrayList<Conversation>();
      while(conversationsSet.next()) {
        Conversation temp = new Conversation(
          Uuids.fromString(conversationsSet.getString(1)), Uuids.fromString(conversationsSet.getString(2)),
          Time.fromMs(conversationsSet.getInt(3)), conversationsSet.getString(4));
        conversations.add(temp);
      }
      return conversations;
    } catch(SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
      return null;
    }
  }

  public ArrayList<Message> restoreMessages() {
    try {
      ResultSet messagesSet = fetchMessages();
      ArrayList<Message> messages = new ArrayList<Message>();
      Uuid last = null;
      if(messagesSet.next()) {
        Uuid id = Uuids.fromString(messagesSet.getString(1));
        Uuid author = Uuids.fromString(messagesSet.getString(3));
        Time time = Time.fromMs(messagesSet.getInt(4));
        String content = messagesSet.getString(5);
        Uuid next = messagesSet.next() ? Uuids.fromString(messagesSet.getString(1)) : null;
        Message temp = new Message(id, next, null, time, author, content);
        messages.add(temp);
        last = id;
      }
      while(messagesSet != null) {
        Uuid id = Uuids.fromString(messagesSet.getString(1));
        Uuid author = Uuids.fromString(messagesSet.getString(3));
        Time time = Time.fromMs(messagesSet.getInt(4));
        String content = messagesSet.getString(5);
        Uuid next = messagesSet.next() ? Uuids.fromString(messagesSet.getString(1)) : null;
        Message temp = new Message(id, next, last, time, author, content);
        messages.add(temp);
        last = id;
      }
      return messages;
    } catch(SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
      return null;
    }
  }
}
