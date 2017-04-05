package codeu.chat;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashSet;

import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.Time;
import codeu.chat.common.User;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import codeu.chat.util.store.Store;


import java.sql.Connection;

public class DerbyStore {
	
	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby://localhost:1527/";
	
	Connection conn;
	Statement stmt;
	
	
	public DerbyStore() {
	
		try {
			// Load the class necessary
			Class.forName(driver).newInstance();
			
			// Connect to the Database
			conn = DriverManager.getConnection(protocol + "chatapp;create=true", null);
			
			// Create a statement object to send queries
			stmt = conn.createStatement();
			
			// If this table exists then we just continue
			// since we don't want to create tables that exist
			stmt.execute("SELECT * FROM chatuser");
			
			// Give confirmation of connection
			System.out.println("Tables exist. Connection made.");
		}
		catch (Exception ex) {
						try {
						// Create the chat user table
						stmt.execute("CREATE TABLE chatuser(id varchar(255), name varchar(255), password varchar(255), creation BIGINT)");
						
						// Create the conversations table
						stmt.execute("CREATE TABLE conversation(id varchar(255), "
								+ "owner varchar(255), creation BIGINT, title varchar(255), users varchar(255), firstMessage varchar(255)," +
						"lastMessage varchar(255))");
						
						// Create the message table
						stmt.execute("CREATE TABLE message(id varchar(255),"
								 + "previous varchar(255), creation BIGINT, author varchar(255), content varchar(255), nextMessage varchar(255))");
						
						// Give confirmation of execution.
						System.out.println("Tables do not exists. Table creation executed.");
						} catch (Exception e) {
							e.printStackTrace();
						}
			//ex.printStackTrace();
		}
	}
	
	public boolean checkForUsername(String username) throws SQLException {
		return stmt.execute("SELECT * FROM chatuser WHERE password = '" + username + "'" );
	}
	
	public boolean checkForPassword(String password) throws SQLException {
		return stmt.execute("SELECT * FROM chatuser WHERE password = '" + password + "'" );
	}
	
	public void addUser(User u) throws SQLException {
		stmt.execute("INSERT INTO chatuser VALUES(" + "\'" + removeCharsInUuid(u.id.toString()) + "\'" +
				"," + "\'" + u.name + "\'" + ", 'test', " + u.creation.inMs() + ")");
	}
	
	public void addMessage(Message m) throws SQLException {
		stmt.execute("INSERT INTO message VALUES(" + "\'" +  removeCharsInUuid(m.id.toString()) +  "\'" +
				","  +  "\'" + removeCharsInUuid(m.previous.toString()) +  "\'" + "," +  m.creation.inMs() + "," 
				+  "\'" + removeCharsInUuid(m.author.toString()) +  "\'" + "," +  "\'" + m.content +  "\'" + "," +  "\'" + removeCharsInUuid(m.next.toString()) +  "\'" + ")");
	}
	
	
	public void addConversation(Conversation c) throws SQLException {
		StringBuilder usersInvolved = new StringBuilder();
		
		for (Uuid s : c.users) {
			usersInvolved.append(removeCharsInUuid(s.toString()));
			usersInvolved.append(" ");
		}
		
		stmt.execute("INSERT INTO conversation VALUES(" +  "\'" + removeCharsInUuid(c.id.toString()) +  "\'" +
				"," +  "\'" + removeCharsInUuid(c.owner.toString()) +  "\'" + "," + c.creation.inMs() + "," + "\'" + c.title + "\'" + "," +  "\'" + usersInvolved.toString() +  "\'" + 
				"," +  "\'" + removeCharsInUuid(c.firstMessage.toString()) +  "\'" +  "," + "\'" + removeCharsInUuid(c.lastMessage.toString()) +  "\'" + ")");
	}
	
	public void updateConversation(Conversation c) throws SQLException {
		stmt.execute("UPDATE conversation SET firstMessage = " + "\'" + removeCharsInUuid(c.firstMessage.toString()) 
			+ "\'" + ", lastMessage = " + "\'" + removeCharsInUuid(c.lastMessage.toString()) + "\'" + " WHERE id = " + "\'" + removeCharsInUuid(c.id.toString()) + "\'");
	}
	
	public void updateLastMessage(Message m) throws SQLException {
		stmt.execute("UPDATE message SET nextMessage = " + "\'" + removeCharsInUuid(m.next.toString()) + "\'" + 
				" WHERE id = " + "\'" + removeCharsInUuid(m.id.toString()) + "\'");
	}
	
	public Store<Uuid, User> getAllUsers() {
		Store<Uuid, User> allUsers = new Store<>(UUID_COMPARE);
		try {
			// Get all of the users in the database
			ResultSet allUsersResponse = stmt.executeQuery("SELECT * FROM chatuser");
			
			
			while (allUsersResponse.next()) {
				
				String uuid = allUsersResponse.getString(1);
				String name = allUsersResponse.getString(2);
				Long creation = allUsersResponse.getLong(4);
				
				// Creation of uuid object from database
				Uuid userid = Uuids.fromString(uuid);
				
				// Creation of time object from database
				Time time = new Time(creation);
				
				User user = new User(userid, name, time);
				
				// Add each created user to the store object
				allUsers.insert(userid, user);
			}
			System.out.println("Successfully retrieved users");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return allUsers;
	}
	
	public Store<Uuid, Conversation> getAllConversations() {
		Store<Uuid, Conversation> allConversations = new Store<>(UUID_COMPARE);
		
		try {
			ResultSet allConversationsResponse = stmt.executeQuery("SELECT * FROM conversation");
			
			
			HashSet<Uuid> ownersUuid = new HashSet<>();
			
			while (allConversationsResponse.next()) {
				
				Uuid conversationid = Uuids.fromString(removeCharsInUuid(allConversationsResponse.getString(1)));
				Uuid ownerUuid = Uuids.fromString(allConversationsResponse.getString(2));
				Time creation = new Time(allConversationsResponse.getLong(3));
				String title = allConversationsResponse.getString(4);
				String owners = allConversationsResponse.getString(5);
				Uuid firstMessage = Uuids.fromString(allConversationsResponse.getString(6));
				Uuid lastMessage = Uuids.fromString(allConversationsResponse.getString(7));
				
				for (String uuid : owners.split(" ")) {
					if (!uuid.isEmpty()) ownersUuid.add(Uuids.fromString(uuid));
				}
				
				
				Conversation c = new Conversation(conversationid, ownerUuid, creation, title, ownersUuid, firstMessage, lastMessage);
				allConversations.insert(conversationid, c);
	
			}
			System.out.println("Successfully retrieved conversations");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return allConversations;
	}
	
	public Store<Uuid, Message> getAllMessages() {
		Store<Uuid, Message> allMessages = new Store<>(UUID_COMPARE);
		
		try {
			ResultSet allMessagesResponse = stmt.executeQuery("SELECT * FROM message");

			while (allMessagesResponse.next()) {
				
				Uuid messageid = Uuids.fromString(allMessagesResponse.getString(1));
				Uuid previous = Uuids.fromString(allMessagesResponse.getString(2));
				Time creation = new Time(allMessagesResponse.getLong(3));
				Uuid author = Uuids.fromString(allMessagesResponse.getString(4));
				String content = allMessagesResponse.getString(5);
				Uuid next = Uuids.fromString(allMessagesResponse.getString(6));
				
				Message m = new Message(messageid, next, previous, creation, author, content);
				allMessages.insert(messageid, m);
					
			}
			System.out.println("Successfully retrieved messages");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return allMessages;
	}
	
	public String removeCharsInUuid(String uuid) {
		// Remove the characters when uuid is transformed to a string
		uuid = uuid.replace("[", "");
		uuid = uuid.replace("]", "");
		uuid = uuid.replace("UUID:", "");
		
		// Prevent a number larger than the max possible int
		// from being passed into the database.
		String[] nums = uuid.split("\\.");
		String largestInt = String.valueOf(Integer.MAX_VALUE);
		StringBuilder collectedUuid = new StringBuilder();
		StringBuilder current = new StringBuilder();
		if (nums.length > 2) {
			for (int x = 0; x < nums[2].length(); x++) {
				current = collectedUuid;
				current.append(nums[2].charAt(x));
					if (largestInt.length() - 1 > current.length()) {
						current = collectedUuid;
					} else break;
			}
			
			uuid = nums[0] + "." + nums[1] + "." + collectedUuid.toString();
		}
		
		return uuid;
	}
	
	
	
	public void shutdownAll() {
		try {
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		}	
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void shutdownADatabase(String databaseName) {
		try {
			DriverManager.getConnection("jdbc:derby:" + databaseName + ";shutdown=true");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// Added this compare function for the store objects used when getting
	// conversations, users, and messages.
	private static final Comparator<Uuid> UUID_COMPARE = new Comparator<Uuid>() {

	    @Override
	    public int compare(Uuid a, Uuid b) {

	      if (a == b) { return 0; }

	      if (a == null && b != null) { return -1; }

	      if (a != null && b == null) { return 1; }

	      final int order = Integer.compare(a.id(), b.id());
	      return order == 0 ? compare(a.root(), b.root()) : order;
	    }
	  };
	
	public static void main(String[] args) {
		
		new DerbyStore();
	} 
}
