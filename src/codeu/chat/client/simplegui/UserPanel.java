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
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.lang.StringBuilder; 

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;

// NOTE: JPanel is serializable, but there is no need to serialize UserPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class UserPanel extends JPanel {

  private final ClientContext clientContext;

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
    userListScrollPane.setMinimumSize(new Dimension(245, 150));
    userListScrollPane.setPreferredSize(new Dimension(245, 150));

    // Current User panel
    final JPanel currentPanel = new JPanel();
    final GridBagConstraints currentPanelC = new GridBagConstraints();

    // Button bar
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();

    final JButton userUpdateButton = new JButton("Update Users");
    final JButton userSignInButton = new JButton("Sign In");
    final JButton userSignOutButton = new JButton("Sign Out");
    final JButton userAddButton = new JButton("Add");
    final JButton userDeleteButton = new JButton("Delete");

    buttonPanel.add(userUpdateButton);
    buttonPanel.add(userSignInButton);
    buttonPanel.add(userSignOutButton); 
    buttonPanel.add(userAddButton);
    buttonPanel.add(userDeleteButton); 

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
    titlePanel.setBackground(new Color(102, 162, 237));
    listShowPanel.setBackground(new Color(102, 162, 237));
    currentPanel.setBackground(new Color(102, 162, 237));
    buttonPanel.setBackground(new Color(102, 162, 237));

    userUpdateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        UserPanel.this.getAllUsers(listModel);
      }
    });

    userSignInButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          //Ask user for password
          final String userPassword = (String) JOptionPane.showInputDialog(
            UserPanel.this, "Enter " + data + "'s password:", "Enter Password", JOptionPane.PLAIN_MESSAGE,
            null, null, "");
          System.out.println("sign in panel: " + userPassword);
          //check password to make sure it is correct and check it against the user's password
          if(clientContext.user.checkPassword(data, userPassword)){ //access userByName, get User, access the user's password (String)
            clientContext.user.signInUser(data);
            userSignedInLabel.setText("Hello " + data);
          } else{
            //user's password was incorrect
            JOptionPane.showMessageDialog(UserPanel.this, "Password for " + data + " was incorrect. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Password for " + data + " was incorrect."); 
          }  
        }
      }
    });
    
    //signs out the user so that someone cannot access your account
    userSignOutButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          clientContext.user.signOutUser();
          userSignedInLabel.setText("Goodbye " + data);
        }
      }
    });

    userAddButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JPanel p = new JPanel();
        JTextField userNameField = new JTextField(10);
        JTextField passwordField = new JPasswordField(10);

        p.add(new JLabel("Enter user name :"));
        p.add(userNameField);
        p.add(new JLabel("Enter password : "));
        p.add(passwordField);

        JOptionPane.showConfirmDialog(null, p, "Add User", JOptionPane.OK_CANCEL_OPTION);
        final String name = userNameField.getText();
        final String password = passwordField.getText();

        if (name != null && name.length() > 0) {
          if(clientContext.user.addUser(name, password)==true){
          	UserPanel.this.getAllUsers(listModel);
          } else{
          	JOptionPane.showMessageDialog(UserPanel.this, "This username is already in use.", "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });
    
    userDeleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (userList.getSelectedIndex() != -1 && clientContext.user.hasCurrent()) {
          final String data = userList.getSelectedValue();

          //remove the user's name from the list

          if (clientContext.user.getCurrent().name.equals(data) && (clientContext.user.deleteUser(data) == true)) {
            //update the user's list and
            UserPanel.this.getAllUsers(listModel);
            userSignedInLabel.setText("Goodbye " + data);
          } else {
            JOptionPane.showMessageDialog(UserPanel.this, "This username cannot be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
            UserPanel.this.getAllUsers(listModel);
          }
        } else {
          JOptionPane.showMessageDialog(UserPanel.this, "There is no user selected to be deleted.", "Error", JOptionPane.ERROR_MESSAGE); 
        }
      }
    });

    userList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          //userInfoPanel.setText(clientContext.user.showUserInfo(data));
        }
      }
    });

    getAllUsers(listModel);
  }

  // Swing UI: populate ListModel object - updates display objects.
  private void getAllUsers(DefaultListModel<String> usersList) {
    clientContext.user.updateUsers();
    usersList.clear();

    for (final User u : clientContext.user.getUsers()) {
      usersList.addElement(u.name);
    }
  }
}
