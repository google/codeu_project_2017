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
//import codeu.chat.common.Uuid;
import codeu.chat.util.store.Store;
import codeu.chat.util.Logger.Log;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;

import java.io.File;
import java.sql.Connection;

public class DerbyStore {
	
	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private String protocol = "jdbc:derby:test";
	
	private Connection conn;
	private Statement stmt;
	
	private final String userTableName = "userInfo";
	private final String conversationTableName = "conversation";
	private final String messageTableName = "message";
	private final String chatParticipantsTableName = "chatParticipants";
	
	private static final Logger.Log LOG = Logger.newLog(ServerMain.class);
	
	
	public DerbyStore() {
	
		try {
			// Load the class necessary
			Class.forName(driver).newInstance();
			
			// Checks to see if the database directory exist
			File database = new File("testchatapp");
					
			// If it does exist then we connect to it, while
			// not overwriting data.
			if (database.exists() && database.isDirectory()) {
				conn = DriverManager.getConnection(protocol + "chatapp;", null);
				stmt = conn.createStatement();
				LOG.info("Tables exist. Connection made.");
				return;
			}
	
			// Connect to the database while creating a new schema
			conn = DriverManager.getConnection(protocol + "chatapp;create=true", null);
			
			// Create a statement object to send queries
			stmt = conn.createStatement();
			
			// Create the chat user table
			stmt.execute("CREATE TABLE " + userTableName + "(id varchar(255), name varchar(255), password varchar(255), creation BIGINT)");
			
			// Create the conversations table
			stmt.execute("CREATE TABLE " + conversationTableName + "(id varchar(255), "
					+ "owner varchar(255), creation BIGINT, title varchar(255), users varchar(255), firstMessage varchar(255)," +
			"lastMessage varchar(255))");
			
			// Create the message table
			stmt.execute("CREATE TABLE " + messageTableName + "(id varchar(255),"
					 + "previous varchar(255), creation BIGINT, author varchar(255), content varchar(255), nextMessage varchar(255))");
			
			stmt.execute("CREATE TABLE " + chatParticipantsTableName + "conversationid varchar(255), userid varchar(255)");
			
			// Give confirmation of execution.
			LOG.info("Tables do not exist. Table creation executed.");
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean checkUsernameExists(String username) throws SQLException {
		return stmt.execute("SELECT * FROM " + userTableName + " WHERE password = '" + username + "'" );
	}
	
	public boolean userLogin(String password, String username) throws SQLException {
		return stmt.execute("SELECT * FROM " + userTableName + " WHERE username = '" + username + "' AND password = '" + password + "'");
	}
	
	public void addUser(User u) throws SQLException {
		stmt.execute("INSERT INTO " + userTableName + " VALUES(" + "\'" + removeCharsInUuid(u.id.toString()) + "\'" +
				"," + "\'" + u.name + "\'" + ", 'test', " + u.creation.inMs() + ")");
	}
	
	public void addMessage(Message m) throws SQLException {
		stmt.execute("INSERT INTO " + messageTableName + " VALUES(" + "\'" +  removeCharsInUuid(m.id.toString()) +  "\'" +
				","  +  "\'" + removeCharsInUuid(m.previous.toString()) +  "\'" + "," +  m.creation.inMs() + "," 
				+  "\'" + removeCharsInUuid(m.author.toString()) +  "\'" + "," +  "\'" + m.content +  "\'" + "," +  "\'" + removeCharsInUuid(m.next.toString()) +  "\'" + ")");
	}
	
	
	public void addConversation(Conversation c) throws SQLException {
		StringBuilder usersInvolved = new StringBuilder();
		
		for (Uuid s : c.users) {
			//usersInvolved.append(removeCharsInUuid(s.toString()));
			//usersInvolved.append(" ");
			
			// Adding chat participants for a specific conversation to a table specifically for it.
			stmt.execute("INSERT INTO " + chatParticipantsTableName + " VALUES('" + c.id + "','" + removeCharsInUuid(s.toString()) + "')");
			System.out.println("INSERT INTO " + chatParticipantsTableName + " VALUES('" + c.id + "','" + removeCharsInUuid(s.toString()) + "')");
		}
		
		// TO DO: Remove participants field from conversations table
		
		
		stmt.execute("INSERT INTO " + conversationTableName + " VALUES(" +  "\'" + removeCharsInUuid(c.id.toString()) +  "\'" +
				"," +  "\'" + removeCharsInUuid(c.owner.toString()) +  "\'" + "," + c.creation.inMs() + "," + "\'" + c.title + "\'" + "," +  "\'" + usersInvolved.toString() +  "\'" + 
				"," +  "\'" + removeCharsInUuid(c.firstMessage.toString()) +  "\'" +  "," + "\'" + removeCharsInUuid(c.lastMessage.toString()) +  "\'" + ")");
	}
	
	public void updateConversation(Conversation c) throws SQLException {
		stmt.execute("UPDATE " + conversationTableName + " SET firstMessage = " + "\'" + removeCharsInUuid(c.firstMessage.toString()) 
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
			ResultSet allUsersResponse = stmt.executeQuery("SELECT * FROM " + userTableName);
			
			
			while (allUsersResponse.next()) {
				
				String uuid = allUsersResponse.getString(1);
				String name = allUsersResponse.getString(2);
				Long creation = allUsersResponse.getLong(4);
				
				// Creation of uuid object from database
				Uuid userid = Uuid.fromString(uuid);
				
				// Creation of time object from database
				Time time = new Time(creation);
				
				User user = new User(userid, name, time);
				
				// Add each created user to the store object
				allUsers.insert(userid, user);
			}
			LOG.info("Accessed users table.");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return allUsers;
	}
	
	public Store<Uuid, Conversation> getAllConversations() {
		Store<Uuid, Conversation> allConversations = new Store<>(UUID_COMPARE);
		
		try {
			ResultSet allConversationsResponse = stmt.executeQuery("SELECT * FROM " + conversationTableName);
			
			HashSet<Uuid> ownersUuid = new HashSet<>();
			
			while (allConversationsResponse.next()) {
				
				Uuid conversationid = Uuid.fromString(removeCharsInUuid(allConversationsResponse.getString(1)));
				
				// Retrieve the users that are a part of the conversation
				ResultSet chatParticipants = stmt.executeQuery("SELECT userid FROM " + chatParticipantsTableName + " WHERE conversationid = " + allConversationsResponse.getString(1));
				
				// Iterate over the participants and them to the hashset
				while (chatParticipants.next()) {
					ownersUuid.add(Uuid.fromString(chatParticipants.getString(1)));
				}
				
				Uuid ownerUuid = Uuid.fromString(allConversationsResponse.getString(2));
				Time creation = new Time(allConversationsResponse.getLong(3));
				String title = allConversationsResponse.getString(4);
				String owners = allConversationsResponse.getString(5);
				Uuid firstMessage = Uuid.fromString(allConversationsResponse.getString(6));
				Uuid lastMessage = Uuid.fromString(allConversationsResponse.getString(7));
				
				/*for (String uuid : owners.split(" ")) {
					if (!uuid.isEmpty()) ownersUuid.add(Uuid.fromString(uuid));
				}*/
				
				
				Conversation c = new Conversation(conversationid, ownerUuid, creation, title, ownersUuid, firstMessage, lastMessage);
				allConversations.insert(conversationid, c);
	
			}
			LOG.info("Accessed conversations table.");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return allConversations;
	}
	
	public Store<Uuid, Message> getAllMessages() {
		Store<Uuid, Message> allMessages = new Store<>(UUID_COMPARE);
		
		try {
			ResultSet allMessagesResponse = stmt.executeQuery("SELECT * FROM " + messageTableName);

			while (allMessagesResponse.next()) {
				
				Uuid messageid = Uuid.fromString(allMessagesResponse.getString(1));
				Uuid previous = Uuid.fromString(allMessagesResponse.getString(2));
				Time creation = new Time(allMessagesResponse.getLong(3));
				Uuid author = Uuid.fromString(allMessagesResponse.getString(4));
				String content = allMessagesResponse.getString(5);
				Uuid next = Uuid.fromString(allMessagesResponse.getString(6));
				
				Message m = new Message(messageid, next, previous, creation, author, content);
				allMessages.insert(messageid, m);
					
			}
			LOG.info("Accessed messages table.");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return allMessages;
	}
	
	// When Uuid is converted to a string
	// it adds characters such as opening
	// and closing brackets. This function removes
	// them, and decreases the different numbers in 
	// the uuid since it could be larger than an int.
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
