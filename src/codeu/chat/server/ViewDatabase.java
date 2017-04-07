package codeu.chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import codeu.chat.common.*;
import codeu.chat.common.Time;
import codeu.chat.util.Logger;
import codeu.chat.util.store.StoreAccessor;
import codeu.chat.common.Uuids;

/**
 * Created by strobe on 4/04/17.
 */
public class ViewDatabase {

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
                        "FROM USERS" +
                        "WHERE  ID = "+SQLFormatter.sqlID(id)+";" );
                while (rs.next()){
                    Uuid userID = Uuids.fromString(rs.getString("ID"));
                    String userName = rs.getString("UNAME");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
                    Date date = sdf.parse(rs.getString("TimeCreated") + ".000");
                    String userPassword = rs.getString("PASSWORD");


                    User user = new User(userID, userName, Time.fromMs(date.getTime()), userPassword);
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

    public Collection<ConversationSummary> getAllConversations() {

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
                Uuid conversationID = Uuids.fromString(rs.getString("ID"));
                Uuid userID = Uuids.fromString(rs.getString("OWNERID"));
                String conversationName = rs.getString("CNAME");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
                Date date = sdf.parse(rs.getString("TimeCreated") + ".000");
                Time conversationTime;


                ConversationSummary conversation = new ConversationSummary(conversationID, userID, Time.fromMs(date.getTime()), conversationName);
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

        /*final Set<User> blacklist = new HashSet<>(intersect(model.userById(), ids));
        final Set<User> users = new HashSet<>();

        for (final User user : model.userById().all()) {
            if (!blacklist.contains(user)) {
                users.add(user);
            }
        }*/

        System.out.println("Accessing ViewDatabase");

        Connection connection = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            connection.setAutoCommit(false);

            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * " +
                    "FROM USERS;" );
            while (rs.next()){
                System.out.println("Enters");
                Uuid userID = Uuids.fromString(rs.getString("ID"));
                String userName = rs.getString("UNAME");
                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
                String userDate = rs.getString("TimeCreated");
                Date date = sdf.parse(userDate);
                String userPassword = rs.getString("PASSWORD");


                User user = new User(userID, userName, Time.fromMs(date.getTime()), userPassword);
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
}
