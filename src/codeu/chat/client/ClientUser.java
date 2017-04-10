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

import java.lang.Object;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.Store;

public final class ClientUser {

  private final static Logger.Log LOG = Logger.newLog(ClientUser.class);

  private static final Collection<Uuid> EMPTY = Arrays.asList(new Uuid[0]);
  private final Controller controller;
  private final View view;

  private User current = null;

  private final Map<String, User> usersByUsername = new HashMap<>();
  private final Map<Uuid, User> usersById = new HashMap<>();

  // This is the set of users known to the server, sorted by name.
  private Store<String, User> usersByName = new Store<>(String.CASE_INSENSITIVE_ORDER);
  private Store<String, User> usersByNickname = new Store<>(String.CASE_INSENSITIVE_ORDER);

  public ClientUser(Controller controller, View view) {
    this.controller = controller;
    this.view = view;
  }

  // TODO: Decide what to do with existed name and nickname

  // Validate the username string
  static public boolean isValidName(String userName) {
    boolean clean = true;
    if (userName.length() == 0) {
      clean = false;
    } else {

      // only accepts names that contain alphabets and spaces.
      Pattern validPattern = Pattern.compile("^[ A-z]+$");
      Matcher match = validPattern.matcher(userName);
      clean = match.matches();

    }
    return clean;
  }

  // Check if this userName has already existed
  public boolean ifExistedName(String userName) {
    final User user = usersByName.first(userName);
    return (user != null);
  }

  // Validate the nickname string
  static public boolean isValidNickname(String nickname) {
    //TODO: check for valid nickname
    return true;
  }
  
  // Check if this nickname has already existed
  public boolean ifExistedNickname(String nickname) {
    // TODO: check for existed nickname
    return false;
  }

  public boolean hasCurrent() {
    return (current != null);
  }

  public User getCurrent() {
    return current;
  }

  public boolean signInUser(String name) {
    return signInUser(name, "");
  }

  public boolean signInUser(String name, String pass) {
    // System.out.println("Pass in signInUser "+ pass);
    updateUsers();

    final User prev = current;
    if (name != null) {
      final User newCurrent = usersByName.first(name);
      final boolean ifCorrectPassword = controller.ifCorrectPassword(newCurrent, pass);
      if (newCurrent != null && ifCorrectPassword) {
        current = newCurrent;
      }
    }
    return (prev != current);
  }

  public boolean signOutUser() {
    boolean hadCurrent = hasCurrent();
    current = null;
    return hadCurrent;
  }

  public void showCurrent() {
    printUser(current);
  }

  public void addUser(String name) {
    addUser(name, "", "");
  }

  public void addUser(String name, String nickname, String pass) {
    final boolean validInputs = isValidName(name) && isValidNickname(nickname);
    final boolean existed = ifExistedName(name) || ifExistedNickname(nickname);
    final boolean valid = (validInputs && !existed);

    final User user = !valid ? null : controller.newUser(name, nickname, pass);

    // System.out.println("in addUser clientUser");

    if (user == null) {
      System.out.format("Error: user not created - %s.\n",
          (!validInputs) ? "bad input value" : ((existed) ? "existed username/nickname" : "server failure"));
    } else {
      LOG.info("New user complete, Name= \"%s\" Nickname= \"%s\" UUID=%s", user.name, user.nickname, user.id);
      usersByUsername.put(user.name, user);
      updateUsers();
    }
  }

  public void addNickname(String name) {
    final boolean validInputs = isValidNickname(name);
    final boolean existed = ifExistedNickname(name);

    final boolean valid = (validInputs && !existed);

    if (current == null){
      System.out.println("Please sign in before setting nickname");
    } else if (!valid) {
      System.out.format("Error: nickname not added - %s.\n",
          (!validInputs) ? "bad input value" : ((existed) ? "existed nickname" : "server failure"));
    } else {
      final boolean response = controller.setNickname(current, name);
      if (response){
        LOG.info("Nickname added complete for the current user, Name= \"%s\" Nickname= \"%s\" UUID=%s", current.name, current.nickname, current.id);
        updateUsers();
      }
      else{
        LOG.info("Cannot set nickname");
      }

  public void deleteUser(String name) {
    
    // check if user exists in the system OR usersById.containsKey(id)

    if (usersByUsername.containsKey(name)) {
      User removeUser = usersByUsername.get(name);
      final User user = controller.deleteUser(name);
      if (user == null) {
        System.out.format("Error: user not deleted - server failure");
      } else {
        LOG.info("Remove user complete, Name=\"%s\" UUID=%s", user.name, user.id);
        usersByUsername.remove(user.name);
        // System.out.format("user by name -%s", usersByName.first(user.name).name);
        // System.out.format("user by id-%s", usersById.get(user.id).name);
        // usersByName.remove(user.name);
        // usersById.remove(user.id);
        updateUsers();
      }
    } else {
      System.out.format("Error: user not deleted - user not found, check input!");
    }
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
    usersByNickname = new Store<>(String.CASE_INSENSITIVE_ORDER);

    for (final User user : view.getUsersExcluding(EMPTY)) {
      usersById.put(user.id, user);
      usersByName.insert(user.name, user);
      usersByNickname.insert(user.nickname, user);
    }
  }

  public static String getUserInfoString(User user) {
    return (user == null) ? "Null user" :
        String.format(" User: %s\n   Nickname: %s\n   Id: %s\n   created: %s\n", user.name, user.nickname, user.id, user.creation);
  }

  public String showUserInfo(String uname) {
    return getUserInfoString(usersByName.first(uname));
  }

  // Move to User's toString()
  public static void printUser(User user) {
    System.out.println(getUserInfoString(user));
  }
}
