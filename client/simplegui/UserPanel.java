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

package codeu.chat.client.simplegui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;

// NOTE: JPanel is serializable, but there is no need to serialize UserPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class UserPanel extends JPanel {

  private final long POLLING_PERIOD_MS = 1000;
  private final long POLLING_DELAY_MS = 0;
  private final ClientContext clientContext;
  private User lastUser;

  public UserPanel(ClientContext clientContext) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    initialize();
  }

  private void initialize() {

    // This panel contains from top to bottom; a title bar, a list of users,
    // information about the current (selected) user, and a button bar.

    // Title bar - also includes name of currently signed-in user.
    final JPanel titlePanel = new JPanel(new GridBagLayout());
    final GridBagConstraints titlePanelC = new GridBagConstraints();

    final JLabel titleLabel = new JLabel("Users", JLabel.LEFT);
    final GridBagConstraints titleLabelC = new GridBagConstraints();
    titleLabelC.gridx = 0;
    titleLabelC.gridy = 0;
    titleLabelC.anchor = GridBagConstraints.PAGE_START;

    final GridBagConstraints titleGapC = new GridBagConstraints();
    titleGapC.gridx = 1;
    titleGapC.gridy = 0;
    titleGapC.fill = GridBagConstraints.HORIZONTAL;
    titleGapC.weightx = 0.9;

    final JLabel userSignedInLabel = new JLabel("not signed in", JLabel.RIGHT);
    final GridBagConstraints titleUserC = new GridBagConstraints();
    titleUserC.gridx = 2;
    titleUserC.gridy = 0;
    titleUserC.anchor = GridBagConstraints.LINE_END;

    titlePanel.add(titleLabel, titleLabelC);
    titlePanel.add(Box.createHorizontalGlue(), titleGapC);
    titlePanel.add(userSignedInLabel, titleUserC);
    titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // User List panel.
    final JPanel listShowPanel = new JPanel();
    final GridBagConstraints listPanelC = new GridBagConstraints();

    final DefaultListModel<String> listModel = new DefaultListModel<>();
    final JList<String> userList = new JList<>(listModel);
    userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userList.setVisibleRowCount(10);
    userList.setSelectedIndex(-1);

    final JScrollPane userListScrollPane = new JScrollPane(userList);
    listShowPanel.add(userListScrollPane);
    userListScrollPane.setPreferredSize(new Dimension(150, 150));

    // Current User panel
    final JPanel currentPanel = new JPanel();
    final GridBagConstraints currentPanelC = new GridBagConstraints();

    final JTextArea userInfoPanel = new JTextArea();
    final JScrollPane userInfoScrollPane = new JScrollPane(userInfoPanel);
    currentPanel.add(userInfoScrollPane);
    userInfoScrollPane.setPreferredSize(new Dimension(245, 85));

    // Button bar
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();

    final JButton userUpdateButton = new JButton("Update");
    final JButton userSignInButton = new JButton("Sign In");
    final JButton userAddButton = new JButton("Add");

    buttonPanel.add(userUpdateButton);
    buttonPanel.add(userSignInButton);
    buttonPanel.add(userAddButton);

    // Placement of title, list panel, buttons, and current user panel.
    titlePanelC.gridx = 0;
    titlePanelC.gridy = 0;
    titlePanelC.gridwidth = 10;
    titlePanelC.gridheight = 1;
    titlePanelC.fill = GridBagConstraints.HORIZONTAL;
    titlePanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    listPanelC.gridx = 0;
    listPanelC.gridy = 1;
    listPanelC.gridwidth = 10;
    listPanelC.gridheight = 8;
    listPanelC.fill = GridBagConstraints.BOTH;
    listPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
    listPanelC.weighty = 0.8;

    currentPanelC.gridx = 0;
    currentPanelC.gridy = 9;
    currentPanelC.gridwidth = 10;
    currentPanelC.gridheight = 3;
    currentPanelC.fill = GridBagConstraints.HORIZONTAL;
    currentPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    buttonPanelC.gridx = 0;
    buttonPanelC.gridy = 12;
    buttonPanelC.gridwidth = 10;
    buttonPanelC.gridheight = 1;
    buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
    buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    this.add(titlePanel, titlePanelC);
    this.add(listShowPanel, listPanelC);
    this.add(buttonPanel, buttonPanelC);
    this.add(currentPanel, currentPanelC);

    userUpdateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final String selected = userList.getSelectedValue();
        UserPanel.this.getAllUsers(listModel, true);
        userList.setSelectedValue(selected, false);
      }
    });

    userSignInButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          clientContext.user.signInUser(data);
          userSignedInLabel.setText("Hello " + data);
        }
      }
    });

    userAddButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final String s = (String) JOptionPane.showInputDialog(
                UserPanel.this, "Enter user name:", "Add User", JOptionPane.PLAIN_MESSAGE,
                null, null, "");
        if (s != null && s.length() > 0) {
          clientContext.user.addUser(s);
          UserPanel.this.getAllUsers(listModel, false);
        }
      }
    });

    userList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          userInfoPanel.setText(clientContext.user.showUserInfo(data));
        }
      }
    });

    getAllUsers(listModel, true);

    // Poll the server for updates
    java.util.Timer userUpdateTimer = new java.util.Timer();
    userUpdateTimer.schedule(new TimerTask() {
      @Override
      public void run() {

        // Remember what user was selected
        final String selected = userList.getSelectedValue();

        // Update the user display panel
        UserPanel.this.getAllUsers(listModel, false);
        
        // Reselect the user
        userList.setSelectedValue(selected, false);
      }
    }, POLLING_DELAY_MS, POLLING_PERIOD_MS);
  }

  // Swing UI: populate ListModel object - updates display objects.
  private void getAllUsers(DefaultListModel<String> usersList, boolean replaceAll){

    // Get all of the users
    clientContext.user.updateUsers();
    try{
         clientContext.user.SerializeUsers();
    }catch(Exception ex){
        
    }
    // If reloading all users, the panel should be empty and there is no last user displayed
    if (replaceAll) {
      usersList.clear();
      lastUser = null;
    }

    // The most recent user that has been displayed
    User newLast = lastUser;

    for (final User u : clientContext.user.getUsers()) {

      // Display the user's name if it is not in the panel yet
      if (replaceAll
              || lastUser == null
              || (u.creation.compareTo(lastUser.creation) >= 0
              && !u.id.equals(lastUser.id))) {

        usersList.addElement(u.name);

        // Remember the most recently displayed user
        if (newLast == null || u.creation.compareTo(newLast.creation) >= 0) {
          newLast = u;
        }
      }
    }
    // Store the last user to be displayed
    lastUser = newLast;
  }
}
