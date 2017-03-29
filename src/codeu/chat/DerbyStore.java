package codeu.chat;

import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.Time;
import codeu.chat.common.User;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import codeu.chat.util.Method;
import codeu.chat.util.store.StoreAccessor;


import java.sql.Connection;

public class DerbyStore {
	
	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby://localhost:1527/";
	
	Connection conn;
	
	
	public DerbyStore() {
		try {
			
			// Load the class necessary
			Class.forName(driver).newInstance();
			
			// Connect to the Database
			conn = DriverManager.getConnection(protocol + "chat;create=true", null);
			
			// Set the SQL Queries to create the necessary tables
			PreparedStatement userTable = conn.prepareStatement("CREATE TABLE chatuser(id varchar(255), name varchar(255), creation BIGINT)");
			PreparedStatement conversationsTable = conn.prepareStatement("CREATE TABLE conversations(id varchar(255), "
					+ "owner varchar(255), creation BIGINT, title varchar(255), users varchar(255), firstMessage varchar(255)," +
			"lastMessage varchar(255))");
			
			PreparedStatement messageTable = conn.prepareStatement("CREATE TABLE message(id varchar(255),"
					 + "previous varchar(255), creation BIGINT, author varchar(255), content varchar(255), nextMessage varchar(255))");
			
			// Execute the Table creation queries
			userTable.execute();
			conversationsTable.execute();
			messageTable.execute();
			

			// Give confirmation of execution
			System.out.println("Executed");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void addUser(User u) throws SQLException {
		System.out.println("-------------------------------  " + u.name +   "   " + u.id + "   " + " --------------------");
		System.out.println("INSERT INTO chatuser VALUES(" + "\'" + u.id + "\'" +
				"," + u.name + "," + u.creation.inMs() + ")");
		PreparedStatement addUser = conn.prepareStatement("INSERT INTO chatuser VALUES(" + "\'" + u.id + "\'" +
				"," + "\'" + u.name + "\'" + "," + u.creation.inMs() + ")");
		addUser.execute();
	}
	
	public void addMessage(Message m) throws SQLException {
		System.out.println("INSERT INTO message VALUES(" + "\'" +  m.id +  "\'" +
				","  +  "\'" + m.previous +  "\'" + "," +  m.creation.inMs() + "," 
				+  "\'" + m.author +  "\'" + "," +  "\'" + m.content +  "\'" + "," +  "\'" + m.next +  "\'" + ")");
		PreparedStatement addMessage = conn.prepareStatement("INSERT INTO message VALUES(" + "\'" +  m.id +  "\'" +
				","  +  "\'" + m.previous +  "\'" + "," +  m.creation.inMs() + "," 
				+  "\'" + m.author +  "\'" + "," +  "\'" + m.content +  "\'" + "," +  "\'" + m.next +  "\'" + ")");
		addMessage.execute();
	}
	
	public void addConversation(Conversation c) throws SQLException {
		StringBuilder usersInvolved = new StringBuilder();
		
		for (Uuid s : c.users) {
			usersInvolved.append(s.toString());
			usersInvolved.append(" ");
		}
		
		System.out.println("-------------- " + usersInvolved.toString() + " ---------");
		
		PreparedStatement addConversation = conn.prepareStatement("INSERT INTO conversations VALUES(" +  "\'" + c.id +  "\'" +
				"," +  "\'" + c.owner +  "\'" + "," + c.creation.inMs() + "," + "\'" + c.title + "\'" + "," +  "\'" + usersInvolved.toString() +  "\'" + 
				"," +  "\'" + c.firstMessage +  "\'" +  "," + "\'" + c.lastMessage +  "\'" + ")");
		
		// public final Uuid id;
		  // public final Uuid owner;
		  // public final Time creation;
		  // public final String title;
		  // public final Collection<Uuid> users = new HashSet<>();
		  // public Uuid firstMessage = Uuids.NULL;
		  // public Uuid lastMessage = Uuids.NULL;
		
		addConversation.execute();
	}
	
	//public StoreAccessor<Uuid, User> getUsers() {}
	
	//public StoreAccessor<Uuid, Conversation> getConversations() {}
	
	//public StoreAccessor<Uuid, Message> getMessages() {}
	
	
	
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
	
	public static void main(String[] args) {
		
		new DerbyStore();
	} 
	
	// Create, Update Users
	  // public final Uuid id;
	  // public final String name;
	  // public final Time creation;
	
	
	// Create, Update Conversations   
	  // public final Uuid id;
	  // public final Uuid owner;
	  // public final Time creation;
	  // public final String title;
	  // public final Collection<Uuid> users = new HashSet<>();
	  // public Uuid firstMessage = Uuids.NULL;
	  // public Uuid lastMessage = Uuids.NULL;
	
    // Create, Update MESSAGE
	  // uuid id
	  // uuid previous
	  // Time creation
	  // uuid author
	  // String content
	  // Uuid next

}
