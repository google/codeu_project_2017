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

public final class ClientUser {

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

// Validate the username string
public boolean isValidName(String userName) {
  boolean isUniqueUser = true; 
  if (userName.trim().length() == 0) {
    isUniqueUser = false;
  } else {
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

  public boolean checkPassword(String name, String password){

    final User user = usersByName.first(name);
    System.out.print("check password: " + user.getPassword());
    printUser(user);

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

  public boolean addUser(String name, String password) {

    final boolean validInputs = isValidName(name);

    final User user = (validInputs) ? controller.newUser(name, password) : null;

    if (user == null) {
      System.out.format("Error: user not created - %s.\n",
          (validInputs) ? "server failure" : "bad input value");
    } else {
      LOG.info("New user complete, Name= \"%s\" UUID=%s", user.name, user.id);
      updateUsers();
      return true;
    }

    return false;
  }

  //delete user method
  public boolean deleteUser(String name) {
    
    //get all users by name
	Iterable <User> users = getUsers();
		
	User target; 
	Uuid targetId; 
	boolean deleteUser = false; 
		
	//find user and get id
	for(User currentUser:users){
	  if(currentUser.name.equals(name)){
	    target = currentUser; 
	    targetId = target.id;
	    
	    deleteUser = controller.deleteUser(target); 
	  
	    if(deleteUser==true){
	      LOG.info("User deleted, Name = \"%s\" UUID = %s", name, targetId);
	      System.out.println("User deleted, Name = " + name); 
	    } else {   	
	      LOG.warning("User not deleted, Name = \"%s\" UUID = %s", name, targetId);
	      System.out.println("Error with deleting User, Name = " + name);
	    }
	    
	    break;
	  } 
	}
	
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
