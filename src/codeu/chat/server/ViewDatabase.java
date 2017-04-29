package codeu.chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;

import codeu.chat.common.*;
import codeu.chat.util.Time;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.StoreAccessor;

/**
 * Created by strobe on 4/04/17.
 */
public final class ViewDatabase {

    private final SQLFormatter sqlFormatter = new SQLFormatter();

    public static Collection<User> getUsers(Collection<Uuid> ids){
        final Collection<User> found = new HashSet<>();

        System.out.println("Accessing ViewDatabase");

        Connection connection = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            connection.setAutoCommit(false);

            for(final Uuid id : ids) {
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery( "SELECT * " +
                        "FROM USERS " +
                        "WHERE  ID = "+SQLFormatter.sqlID(id)+";" );
                while (rs.next()){
                    Uuid userID = Uuid.fromString(rs.getString("ID"));
                    String userName = rs.getString("UNAME");
                    Time creationTime = Time.fromMs(rs.getLong("TimeCreated"));
                    String userPassword = rs.getString("PASSWORD");


                    User user = new User(userID, userName, creationTime, userPassword);
                    found.add(user);
                }
                rs.close();
                stmt.close();
            }

            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return found;
    }

    public static Collection<Conversation> getConversations(Collection<Uuid> ids){
        final Collection<Conversation> found = new HashSet<>();

        System.out.println("Accessing ViewDatabase");

        Connection connection = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            connection.setAutoCommit(false);

            for(final Uuid id : ids) {
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery( "SELECT * " +
                        "FROM CONVERSATIONS " +
                        "WHERE  ID = "+SQLFormatter.sqlID(id)+";" );
                while (rs.next()){
                    Uuid conversationID = Uuid.fromString(rs.getString("ID"));
                    String conversationName = rs.getString("CNAME");
                    Time creationTime = Time.fromMs(rs.getLong("TimeCreated"));
                    Uuid ownerID = Uuid.fromString(rs.getString("OWNERID"));


                    Conversation conversation = new Conversation(conversationID, ownerID, creationTime, conversationName);
                    found.add(conversation);
                }
                rs.close();
                stmt.close();
            }

            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return found;
    }

    public static Collection<Message> getMessages(Collection<Uuid> ids){
        final Collection<Message> found = new HashSet<>();

        System.out.println("Accessing ViewDatabase");

        Connection connection = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            connection.setAutoCommit(false);

            for(final Uuid id : ids) {
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery( "SELECT * " +
                        "FROM MESSAGES " +
                        "WHERE ID = "+SQLFormatter.sqlID(id)+";" );
                while (rs.next()){
                    Uuid messageID = Uuid.fromString(rs.getString("ID"));
                    Uuid nextMessageID = Uuid.fromString(rs.getString("MNEXTID"));
                    Uuid prevMessageID = Uuid.fromString(rs.getString("PNEXTID"));
                    Time creationTime = Time.fromMs(rs.getLong("TimeCreated"));
                    Uuid authorID = Uuid.fromString(rs.getString("USERID"));
                    String content = rs.getString("MESSAGE");

                    Message message = new Message(messageID, nextMessageID, prevMessageID, creationTime, authorID, content);
                    found.add(message);
                }
                rs.close();
                stmt.close();
            }

            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return found;
    }

    public static Collection<ConversationSummary> getAllConversations() {

        final Collection<ConversationSummary> summaries = new ArrayList<>();

        Connection connection = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            connection.setAutoCommit(false);

            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * " +
                    "FROM CONVERSATIONS;" );
            while (rs.next()){
                Uuid conversationID = Uuid.fromString(rs.getString("ID"));
                Uuid userID = Uuid.fromString(rs.getString("OWNERID"));
                String conversationName = rs.getString("CNAME");
                Time creationTime = Time.fromMs(rs.getLong("TimeCreated"));

                ConversationSummary conversation = new ConversationSummary(conversationID, userID, creationTime, conversationName);
                summaries.add(conversation);
            }

            rs.close();
            stmt.close();

            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return summaries;

    }

    public static Collection<User> getUsersExcluding(Collection<Uuid> ids) {

        final Set<User> users = new HashSet<>();

        System.out.println("Accessing ViewDatabase");

        Connection connection = null;
        Statement stmt = null;
        boolean flag = true;

        String parameters;

        if(!ids.isEmpty()){
            parameters = "WHERE ";

            for (final Uuid id : ids) {

                String restricted = id.toString();
                if(flag){
                    parameters = parameters + "ID <> " + restricted;
                }
                else{
                    parameters = parameters + " AND ID <> " + restricted;
                }
            }
        }else {
            parameters = "";
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            connection.setAutoCommit(false);

            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * " +
                    "FROM USERS "+
                    parameters + ";" );
            while (rs.next()){
                Uuid userID = Uuid.fromString(rs.getString("ID"));
                String userName = rs.getString("UNAME");
                Time creationTime = Time.fromMs(rs.getLong("TimeCreated"));
                String userPassword = rs.getString("PASSWORD");


                User user = new User(userID, userName, creationTime, userPassword);
                users.add(user);
            }
            rs.close();
            stmt.close();

            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return users;
    }

    public static Collection<Conversation> getConversations(Time start, Time end){
        final Collection<Conversation> found = new ArrayList<>();

        Connection connection = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            connection.setAutoCommit(false);



            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * " +
                    "FROM CONVERSATIONS "+
                    "WHERE TimeCreated > " + SQLFormatter.sqlCreationTime(start) +
                    " AND TimeCreated < " + SQLFormatter.sqlCreationTime(end) +
                    " ORDER BY TimeCreated ASC;" );
            while (rs.next()){
                Uuid conversationID = Uuid.fromString(rs.getString("ID"));
                Uuid userID = Uuid.fromString(rs.getString("OWNERID"));
                String conversationName = rs.getString("CNAME");
                Time creationTime = Time.fromMs(rs.getLong("TimeCreated"));

                Conversation conversation = new Conversation(conversationID, userID, creationTime, conversationName);
                found.add(conversation);
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return found;
    }

    public static Collection<Conversation> getConversations(String filter) {

        final Collection<Conversation> found = new ArrayList<>();

        Connection connection = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            connection.setAutoCommit(false);



            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * " +
                    "FROM CONVERSATIONS "+
                    "WHERE CNAME LIKE '%" + filter + "%';" );

            while (rs.next()){
                Uuid conversationID = Uuid.fromString(rs.getString("ID"));
                Uuid userID = Uuid.fromString(rs.getString("OWNERID"));
                String conversationName = rs.getString("CNAME");
                Time creationTime = Time.fromMs(rs.getLong("TimeCreated"));

                Conversation conversation = new Conversation(conversationID, userID, creationTime, conversationName);
                found.add(conversation);
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return found;
    }

    public static Collection<Message> getMessages(Uuid conversation, Time start, Time end) {

        final Collection<Message> found = new ArrayList<>();

        Connection connection = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            connection.setAutoCommit(false);



            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * " +
                    "FROM CONVERSATIONS "+
                    "WHERE TimeCreated > " + SQLFormatter.sqlCreationTime(start) +
                    " AND TimeCreated < " + SQLFormatter.sqlCreationTime(end) +
                    " AND CONVERSATIONID = " +SQLFormatter.sqlID(conversation)+
                    " ORDER BY TimeCreated ASC;" );

            while (rs.next()){
                Uuid messageID = Uuid.fromString(rs.getString("ID"));
                Uuid nextMessageID = Uuid.fromString(rs.getString("MNEXTID"));
                Uuid prevMessageID = Uuid.fromString(rs.getString("PNEXTID"));
                Time creationTime = Time.fromMs(rs.getLong("TimeCreated"));
                Uuid authorID = Uuid.fromString(rs.getString("USERID"));
                String content = rs.getString("MESSAGE");

                Message message = new Message(messageID, nextMessageID, prevMessageID, creationTime, authorID, content);
                found.add(message);
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return found;
    }
}
