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
import java.util.HashMap;
import java.util.Map;

import codeu.chat.common.Group;
import codeu.chat.common.GroupSummary;
import codeu.chat.util.Uuid;
import codeu.chat.util.Logger;
import codeu.chat.util.Method;
import codeu.chat.util.store.Store;

public final class ClientGroup {

  private final static Logger.Log LOG = Logger.newLog(ClientGroup.class);

  private final Controller controller;
  private final View view;

  private GroupSummary currentSummary = null;
  private Group currentGroup = null;

  private final ClientUser userContext;
  private ClientGroup groupContext = null;

  private ClientConversation conversationContext = null;

  // This is the set of groups known to the server.
  private final Map<Uuid, GroupSummary> summariesByUuid = new HashMap<>();

  // This is the set of groups known to the server, sorted by title.
  private Store<String, GroupSummary> summariesSortedByTitle =
      new Store<>(String.CASE_INSENSITIVE_ORDER);

  public ClientGroup(Controller controller, View view, ClientUser userContext) {
    this.controller = controller;
    this.view = view;
    this.userContext = userContext;
  }

  public void setGroupContext(ClientGroup groupContext) {
    this.groupContext = groupContext;
  }

  public void setConversationContext(ClientConversation conversationContext) {
    this.conversationContext = conversationContext;
  }

  // Validate the title of the conversation
  static public boolean isValidTitle(String title) {
    boolean clean = true;
    if ((title.length() <= 0) || (title.length() > 64)) {
      clean = false;
    } else {

      // TODO: check for invalid characters

    }
    return clean;
  }

  public boolean hasCurrent() {
    return (currentSummary != null);
  }

  public GroupSummary getCurrent() {
    return currentSummary;
  }

  public Uuid getCurrentId() { return (currentSummary != null) ? currentSummary.id : null; }

  public void showCurrent() {
    printGroup(currentSummary, userContext);
  }

  public void startGroup(String title, Uuid owner) {
    final boolean validInputs = isValidTitle(title);

    final Group group = (validInputs) ? controller.newGroup(title, owner) : null;

    if (group == null) {
      System.out.format("Error: group not created - %s.\n",
          (validInputs) ? "server failure" : "bad input value");
    } else {
      LOG.info("New group: Title= \"%s\" UUID= %s", group.title, group.id);

      currentSummary = group.summary;

      updateAllGroups(currentSummary != null);
    }
  }

  public void setCurrent(GroupSummary group) { currentSummary = group; }

  public void showAllGroups() {
    updateAllGroups(false);

    for (final GroupSummary c : summariesByUuid.values()) {
      printGroup(c, userContext);
    }
  }

  // Get a single group from the server.
  public Group getGroup(Uuid groupId) {
    for (final Group g : view.getGroups(Arrays.asList(groupId))) {
      return g;
    }
    return null;
  }

  private void joinGroup(String match) {
    Method.notImplemented();
  }

  private void leaveCurrentGroup() {
    Method.notImplemented();
  }

  private void updateCurrentGroup() {
    if (currentSummary == null) {
      currentGroup = null;
    } else {
      currentGroup = getGroup(currentSummary.id);
      if (currentGroup == null) {
        LOG.info("GetGroup: current=%s, current.id=%s, but currentConversation == null",
            currentSummary, currentSummary.id);
      } else {
        LOG.info("Get Group: Title=\"%s\" UUID=%s first=%s last=%s\n",
            currentGroup.title, currentGroup.id, currentGroup.firstConversation,
            currentGroup.lastConversation);
      }
    }
  }

  public int groupsCount() {
   return summariesByUuid.size();
  }

  public Iterable<GroupSummary> getGroupSummaries() {
    return summariesSortedByTitle.all();
  }

  // Update the list of known Groups.
  // If the input currentChanged is true, then re-establish the state of
  // the current Group, including its conversations.
  public void updateAllGroups(boolean currentChanged) {

    summariesByUuid.clear();
    summariesSortedByTitle = new Store<>(String.CASE_INSENSITIVE_ORDER);

    for (final GroupSummary gs : view.getAllGroups()) {
      summariesByUuid.put(gs.id, gs);
      summariesSortedByTitle.insert(gs.title, gs);
    }

    if (currentChanged) {
      updateCurrentGroup();
      conversationContext.resetCurrent(true);
      //groupContext.resetCurrent(true);
    }
  }

  // Print Group.  User context is used to map from owner UUID to name.
  public static void printGroup(GroupSummary g, ClientUser userContext) {
    if (g == null) {
      System.out.println("Null group");
    } else {
      final String name = (userContext == null) ? null : userContext.getName(g.owner);
      final String ownerName = (name == null) ? "" : String.format(" (%s)", name);
      System.out.format(" Title: %s\n", g.title);
      System.out.format("    Id: %s owner: %s%s created %s\n", g.id, g.owner, ownerName, g.creation);
    }
  }

  // Print Group outside of User context.
  public static void printGroup(GroupSummary g) {
    printGroup(g, null);
  }
}
