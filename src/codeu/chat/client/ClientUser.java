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

import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;
import codeu.chat.util.store.Store;

import codeu.chat.authentication.AuthenticationCode;

public final class ClientUser {

  private final static Logger.Log LOG = Logger.newLog(ClientUser.class);

  private static final Collection<Uuid> EMPTY = Arrays.asList(new Uuid[0]);
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

  public boolean hasCurrent() {
    return (current != null);
  }

  public User getCurrent() {
    return current;
  }

  public boolean signInUser(String username, String password) {
    current = controller.login(username, password);

    if (current != null) {
      LOG.info("Login complete, Name= \"%s\"", username);
      return true;
    } else {
      System.out.println("Error: invalid login");
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

  public boolean addUser(String username, String password) {
    final int result = controller.newUser(username, password);

    if (result == AuthenticationCode.SUCCESS) {
      LOG.info("New user complete, Name= \"%s\"", username);
      return true;
    } else {
      switch(result) {
        case AuthenticationCode.REGISTER_USER_EXISTS:
          System.out.println("Error: user already exists");
          break;
        case AuthenticationCode.REGISTER_INVALID_INPUT:
          System.out.println("Error: invalid input");
          break;
        default:
          System.out.println("Error: server error");
          break;
      }
    }

    return false;
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
