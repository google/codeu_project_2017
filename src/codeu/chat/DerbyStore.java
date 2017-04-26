package codeu.chat;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashSet;

import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.util.Time;
import codeu.chat.common.User;
import codeu.chat.util.store.Store;
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
	
	private final String checkUsername = "SELECT * FROM " + userTableName + " WHERE name = ?";
	private final String checkValidUser = "SELECT * FROM " + userTableName + " WHERE name = ? AND password = ?";
	
	private final String addUserInfo = "INSERT INTO " + userTableName + " (id, name, password, creation) values (?, ?, ?, ?)";
	private final String addConversationInfo = "INSERT INTO " + conversationTableName + " (id, owner, creation, title, firstMessage, lastMessage) " + 
			"values (?, ?, ?, ?, ?, ?)";
	private final String addMessageInfo = "INSERT INTO " + messageTableName + " (id, previous, creation, author, content, nextMessage) " + 
	"values (?, ?, ?, ?, ?, ?)";
	private final String addChatParticipantsInfo = "INSERT INTO " + chatParticipantsTableName + " (conversationid, userid) values (?, ?)";

	
	private final String updateLastMessageById = "UPDATE " + messageTableName + " SET nextMessage = ? WHERE id = ?";
	private final String updateConversationById = "UPDATE " + conversationTableName + " SET firstMessage = ?, lastMessage = ? WHERE id = ?";
	
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
			
			stmt.execute("CREATE TABLE " + chatParticipantsTableName + "(conversationid varchar(255), userid varchar(255))");
			
			// Give confirmation of execution.
			LOG.info("Tables do not exist. Table creation executed.");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean checkUsernameExists(String name) throws SQLException {
		PreparedStatement checkUserTest = conn.prepareStatement(checkUsername);
		checkUserTest.setString(1, name);
		ResultSet user = checkUserTest.executeQuery();
		
		return user == null;
	}
	
	public String userLoginCheck(String name, String password) throws SQLException {
		PreparedStatement checkValidUserTest = conn.prepareStatement(checkValidUser);
		checkValidUserTest.setString(1, name);
		checkValidUserTest.setString(2, password);
		ResultSet user = checkValidUserTest.executeQuery();
		
		return (user != null) ? user.getString(1) : null;
	}
	
	public void addUser(User u) throws SQLException {
		PreparedStatement addUser = conn.prepareStatement(addUserInfo);
		addUser.setString(1, removeCharsInUuid(u.id.toString()));
		addUser.setString(2, u.name);
		addUser.setString(3, "test");
		addUser.setLong(4, u.creation.inMs());
		
		addUser.executeUpdate();
	}
	
	public void addMessage(Message m) throws SQLException {
		PreparedStatement addMessage = conn.prepareStatement(addMessageInfo);
		addMessage.setString(1, removeCharsInUuid(m.id.toString()));
		addMessage.setString(2, removeCharsInUuid(m.previous.toString()));
		addMessage.setLong(3, m.creation.inMs());
		addMessage.setString(4, removeCharsInUuid(m.author.toString()));
		addMessage.setString(5, m.content);
		addMessage.setString(6, removeCharsInUuid(m.next.toString()));
		
		addMessage.executeUpdate();
	}
	
	
	public void addConversation(Conversation c) throws SQLException {
		
		for (Uuid s : c.users) {
			// Adding chat participants for a specific conversation to a table specifically for it.
			PreparedStatement addChatParticipants = conn.prepareStatement(addChatParticipantsInfo);
			addChatParticipants.setString(1, removeCharsInUuid(c.id.toString()));
			addChatParticipants.setString(2, removeCharsInUuid(s.toString()));
			addChatParticipants.executeUpdate();
		
			LOG.info("INSERT INTO " + chatParticipantsTableName + " VALUES('" + removeCharsInUuid(c.id.toString()) + "','" + removeCharsInUuid(s.toString()) + "')");
		}

		PreparedStatement addConversation = conn.prepareStatement(addConversationInfo);
		addConversation.setString(1, removeCharsInUuid(c.id.toString()));
		addConversation.setString(2, removeCharsInUuid(c.owner.toString()));
		addConversation.setLong(3, c.creation.inMs());
		addConversation.setString(4, c.title);
		addConversation.setString(5, removeCharsInUuid(c.firstMessage.toString()));
		addConversation.setString(6, removeCharsInUuid(c.lastMessage.toString()));
		addConversation.executeUpdate();
		
	}
	
	public void updateConversation(Conversation c) throws SQLException {
		
		PreparedStatement updateConversationStatement = conn.prepareStatement(updateConversationById);
		
		updateConversationStatement.setString(1, removeCharsInUuid(c.firstMessage.toString()));
		updateConversationStatement.setString(2, removeCharsInUuid(c.lastMessage.toString()));
		updateConversationStatement.setString(3, removeCharsInUuid(c.id.toString()));
		
		updateConversationStatement.executeUpdate();
		
	}
	
	public void updateLastMessage(Message m) throws SQLException {
		PreparedStatement updateMessageStatement = conn.prepareStatement(updateLastMessageById);
		
		updateMessageStatement.setString(1, removeCharsInUuid(m.next.toString()));
		updateMessageStatement.setString(2, removeCharsInUuid(m.id.toString()));
		
		updateMessageStatement.executeUpdate();
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
				Uuid userid = Uuid.parse(uuid);
				
				// Creation of time object from database
				Time time = Time.fromMs(creation);
				
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
			final Statement partipantsStatement = conn.createStatement();
			
			ResultSet allConversationsResponse = stmt.executeQuery("SELECT * FROM " + conversationTableName);
			
			HashSet<Uuid> ownersUuid = new HashSet<>();
			
			while (allConversationsResponse.next()) {
				
				Uuid conversationid = Uuid.fromString(removeCharsInUuid(allConversationsResponse.getString(1)));
				
				// Retrieve the users that are a part of the conversation
				ResultSet chatParticipants = partipantsStatement.executeQuery("SELECT userid FROM " + chatParticipantsTableName + " WHERE conversationid = '" + allConversationsResponse.getString(1) + "'");
				
				// Iterate over the participants and them to the hashset
				while (chatParticipants.next()) {
					ownersUuid.add(Uuid.fromString(chatParticipants.getString(1)));
				}
				
				Uuid ownerUuid = Uuid.fromString(allConversationsResponse.getString(2));
				Time creation = new Time(allConversationsResponse.getLong(3));

				String title = allConversationsResponse.getString(4);
				String owners = allConversationsResponse.getString(5);
				Uuid firstMessage = Uuid.parse(allConversationsResponse.getString(6));
				Uuid lastMessage = Uuid.parse(allConversationsResponse.getString(7));
				
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
				
				Uuid messageid = Uuid.parse(allMessagesResponse.getString(1));
				Uuid previous = Uuid.parse(allMessagesResponse.getString(2));
				Time creation = Time.fromMs(allMessagesResponse.getLong(3));
				Uuid author = Uuid.parse(allMessagesResponse.getString(4));
				String content = allMessagesResponse.getString(5);
				Uuid next = Uuid.parse(allMessagesResponse.getString(6));
				
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
