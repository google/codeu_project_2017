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

package codeu.chat.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.*; 

import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.Store;

public class ClientUser {

  private final static Logger.Log LOG = Logger.newLog(ClientUser.class);

  //private static final Collection<Uuid> EMPTY = Arrays.asList(new Uuid[0]);
  private static Collection<Uuid> EMPTY = new ArrayList<Uuid>(); 
  private final Controller controller;
  private final View view;

  private User current = null;

  private final Map<Uuid, User> usersById = new HashMap<>();

  // This is the set of users known to the server, sorted by name.
  private Store<String, User> usersByName = new Store<>(String.CASE_INSENSITIVE_ORDER);

  public ClientUser(Controller controller, View view) {
    this.controller = controller;
    this.view = view;
  }
  
  public Store<String, User> getUsersByName(){
    return usersByName; 
  }

  /*
   * Validates the username string.
   *
   * Returns a boolean stating whether the given username
   * is a valid username. Usernames are valid if they their non-space
   * characters do not match any other usernames (case-insensitive).
   *
   * @param userName
   * @return boolean stating whether username is valid
   */
public boolean isValidName(String userName) {
  updateUsers(); //pull information from the server 
  boolean isUniqueUser = true;
  if (userName.trim().length() == 0) {
    isUniqueUser = false;
  } else {
    // test if each existing username matches
    for (User currentUser : getUsers()) {
      if(currentUser.name.toUpperCase().equals(userName.toUpperCase())){
        System.out.format("Error: user not created - %s already exists.", userName);
        isUniqueUser = false;
      }
    }
  }
  return isUniqueUser;
}
  
  public boolean hasCurrent() {
    return (current != null);
  }

  public User getCurrent() {
    return current;
  }

  public boolean signInUser(String name) {
    updateUsers();

    final User prev = current;
    if (name != null) {
      final User newCurrent = usersByName.first(name);
      if (newCurrent != null) {
        current = newCurrent;
      }
    }
    return (prev != current);
  }


  /*
  * Checks whether a provided password is the correct password
  *
  * Given a name and password, returns true if the password
  * is the password corresponding to name, and false otherwise.
  *
  * @param name name of user whose password to check
  * @param password password to compare with name's password
  * @return boolean stating whether password is name's password
  */
  public boolean checkPassword(String name, String password){

    final User user = getUserNameStore().first(name);

    if( user != null  && (user.getPassword().equals(password))) {
      return true;
    }

    return false;

  }

  public boolean signOutUser() {
    boolean hadCurrent = hasCurrent();
    current = null;
    return hadCurrent;
  }

  public void showCurrent() {
    printUser(current);
  }



  /**
   * Adds user to the server and returns whether
   * user was successfully added
   *
   * Attempts to add the user to the collection of users on the server,
   * prints an error message if the user is not successfully added
   * and returns a boolean stating whether the user was successfully added
   *
   * @param  name name of user to be added to server
   * @param password password of user to be added to server
   * @return boolean stating whether user was successfully added to the server
   */
  public boolean addUser(String name, String password) {

    final boolean validInputs = isValidName(name);

    final User user = (validInputs) ? controller.newUser(name, password) : null;

    // prints error if server fails or input was invalid
    if (user == null) {
      System.out.format("Error: user not created - %s.\n",
          (validInputs) ? "server failure" : "bad input value");
    } else {
      LOG.info("New user complete, Name= \"%s\" UUID=%s", user.name, user.id);
      updateUsers();
      return true;
    }

    // returns false if new user was not successfully created
    return false;
  }

  /**
   * Attempts to delete a user from the server given the name of a user and returns a boolean
   * stating whether it was successfully deleted.
   *
   * Given the name of a user to delete, finds the corresponding id and calls deleteUser in controller to
   * attempts to delete the user from the collection of users on the server, prints messages to the server
   * and client stating whether the user was successfully deleted,
   * and returns a boolean stating whether the user was successfully added
   *
   * @param  name name of user to be deleted
   * @return boolean stating whether user was successfully deleted from the server
   */
  public boolean deleteUser(String name) {
    
    //get all users by name
	Iterable <User> users = getUsers();
		
	User target; 
	Uuid targetId; 
	boolean deleteUser = false; 
		
	//find user and get id
	for(User currentUser:users){
	  // check if each user's name matches the given name
	  if(currentUser.name.equals(name)){
	    target = currentUser; 
	    targetId = target.id;

	    // calls controller's deleteUser, which attempts to delete the user from
        // the server, true if user was deleted from server
	    deleteUser = controller.deleteUser(target); 
	  
	    if(deleteUser==true){
	      // prints information if user was deleted
	      LOG.info("User deleted, Name = \"%s\" UUID = %s", name, targetId);
	      System.out.println("User deleted, Name = " + name); 
	    } else {
	      // prints information if user was not deleted
	      LOG.warning("User not deleted, Name = \"%s\" UUID = %s", name, targetId);
	      System.out.println("Error with deleting User, Name = " + name);
	    }
	    
	    break;
	  } 
	}

	// returns whether user was deleted
	return deleteUser;  
  }
  

  public void showAllUsers() {
    updateUsers();
    for (final User u : usersByName.all()) {
      printUser(u);
    }
  }
  
  public User lookup(Uuid id) {
    return (usersById.containsKey(id)) ? usersById.get(id) : null;
  }

  public String getName(Uuid id) {
    final User user = lookup(id);
    if (user == null) {
      LOG.warning("userContext.lookup() failed on ID: %s", id);
      return null;
    } else {
      return user.name;
    }
  }

  public Iterable<User> getUsers() {
    return usersByName.all();
  }

  public Store<String, User> getUserNameStore(){ return usersByName;}
  
  public void updateUsers() {
    usersById.clear();
    usersByName = new Store<>(String.CASE_INSENSITIVE_ORDER);

    for (final User user : view.getUsersExcluding(EMPTY)) {
      usersById.put(user.id, user);
      usersByName.insert(user.name, user);
    }
  }

  public static String getUserInfoString(User user) {
    return (user == null) ? "Null user" :
        String.format(" User: %s\n   Id: %s\n   created: %s\n", user.name, user.id, user.creation);
  }

  public String showUserInfo(String uname) {
    return getUserInfoString(usersByName.first(uname));
  }

  // Move to User's toString()
  public static void printUser(User user) {
    System.out.println(getUserInfoString(user));
  }
}
