package codeu.chat;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;


import java.sql.Connection;

public class DerbyStore {
	
	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby://localhost:1527/";
	
	
	public DerbyStore() {
		try {
			// Load the class neccesary
			Class.forName(driver).newInstance();
			
			// Connect to the Database
			Connection conn = DriverManager.getConnection(protocol + "chat;create=true", null);
			
			// Set the SQL Queries to create the neccesary tables
			PreparedStatement userTable = conn.prepareStatement("CREATE TABLE User(userid varchar(255), name varchar(64))");
			PreparedStatement conversationsTable = conn.prepareStatement("CREATE TABLE Conversations(conversationsid varchar(255), "
					+ "name varchar(64))");
			PreparedStatement conversationTable = conn.prepareStatement("CREATE TABLE Conversation(conversationid varchar(255), "
					+ "conversationsid varchar(255), content varchar(255))");
			
			// Execute the Table creation queries
			userTable.execute();
			conversationsTable.execute();
			conversationTable.execute();

			// Give confirmation of execution
			System.out.println("Executed");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//public createAllTables()
	
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
	// Create, Update Conversations
	// Create, Update Converation

}
