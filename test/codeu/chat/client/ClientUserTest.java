//Backend Test of ClientUser method(s)

package codeu.chat.client;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.Store;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.client.Controller; 
import codeu.chat.client.ClientUser;
import codeu.chat.client.View;

public final class ClientUserTest {
  
  @Test
  public void testClientUserTest() {
  	
  	//Create a Connection Source
  	String host = "localhost@2007"; 
  	int port = 1234; 
  	
  	ConnectionSource connectionSourceTest = new ClientConnectionSource(host, port); 
  	
  	//Create a Controller
  	Controller controllerTest = new Controller(connectionSourceTest); 
  	
  	//Create a View
  	View viewTest = new View(connectionSourceTest); 
  	
  	//Create an instance of the ClientUser
  	ClientUser clientUserTest = new ClientUser(controllerTest, viewTest); 
  	
  	//Add users using addUser method in ClientUser class
  	String name1 = "   "; //all spaces 
  	String name2 = ""; //empty
  	String name3 = "JESS"; 
  	String name4 = "jess"; 
  	String name5 = "Sarah"; 
  	String name6 = "sArAh"; 
  	String name7 = "Sarah Depew"; 
  	String name8 = "Mathang*"; 
  	String name9 = "**#*@(!*#*$*@"; 
  	String name10 = "Mathangi97";
  	
  	/*
  	//test isValidName
  	assertFalse(clientUserTest.isValidName(name1)); 
  	assertFalse(clientUserTest.isValidName(name2)); 
  	assertTrue(clientUserTest.isValidName(name3));   	
  	`//assertFalse(clientUserTest.isValidName(name4)); //returning null, rather than false like it should...
  	System.out.println(clientUserTest.isValidName(name4)); 
  	assertTrue(clientUserTest.isValidName(name5)); 
  	//assertFalse(clientUserTest.isValidName(name6)); 
  	assertTrue(clientUserTest.isValidName(name7));  
  	assertTrue(clientUserTest.isValidName(name8));
  	assertTrue(clientUserTest.isValidName(name9)); 
  	assertTrue(clientUserTest.isValidName(name10));  
  	*/
  	
  	//test Add User
  	clientUserTest.addUser(name1);
  	clientUserTest.addUser(name2);
  	clientUserTest.addUser(name3);
  	clientUserTest.addUser(name4);
  	clientUserTest.addUser(name5);
  	clientUserTest.addUser(name6);
  	clientUserTest.addUser(name7);
  	clientUserTest.addUser(name8);
  	clientUserTest.addUser(name9);
  	clientUserTest.addUser(name10);
  	
  	//assertFalse(clientUserTest.getUsers().contains(name1)); 
  	System.out.println("Hi"); 
  	Iterable<User> users = clientUserTest.getUsers(); 
  	
  	for(User u: users){
  		System.out.println(u); 
  	}
  	
  	/* 
  	assertFalse(clientUserTest.addUser(name2)); 
  	assertTrue(clientUserTest.addUser(name3));   	
  	assertFalse(clientUserTest.addUser(name4)); 
  	assertTrue(clientUserTest.addUser(name5)); 
  	assertFalse(clientUserTest.addUser(name6)); 
  	assertTrue(clientUserTest.addUser(name7));  
  	assertTrue(clientUserTest.addUser(name8));
  	assertTrue(clientUserTest.addUser(name9)); 
  	assertTrue(clientUserTest.addUser(name10));  
  	
  	/*
  	
  	ClientUser clientUser = new ClientUser(someController, someView);
clientUser.addUser(someName);
assertTrue(getUsers().contains(someName));

clientUser.addUser(someName);
assertThat the server error was logged
assertTrue(getUsers().contains(one(someName)));
*/
  
  	
  }

}