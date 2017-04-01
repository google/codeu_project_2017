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
import java.util.Arrays;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;
import codeu.chat.client.Password;

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
   // final

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
        UserPanel.this.getAllUsers(listModel);
      }
    });

    userSignInButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          int i=0; int MAX_TRIALS=3;
          while(true) {
              JPanel signinPanel=new JPanel(new GridBagLayout());
              GridBagConstraints constraints = new GridBagConstraints();
              constraints.anchor = GridBagConstraints.WEST;
              constraints.insets = new Insets(10, 10, 10, 10);

              String message = (i==0) ? "Enter Password" : "TryAgain";
              JLabel pLabel=new JLabel(message);
              constraints.gridx = 0;
              constraints.gridy = 0;
              signinPanel.add(pLabel, constraints);

              JPasswordField pField=new JPasswordField();
              pField.setColumns(16);
              constraints.gridx=1;
              signinPanel.add(pField, constraints);

              String[] options = new String[]{"OK", "Cancel"};
              int option = JOptionPane.showOptionDialog(null, signinPanel, "Sign-in", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
              if (option == 0 ) {
                  final String password = String.valueOf(pField.getPassword());
                  if (password != null && password.length() > 0) {
                      if (Password.authenticateUserGUI(data, password)) {
                          clientContext.user.signInUser(data, 1);
                          userSignedInLabel.setText("Hello " + data);
                          break;
                      }
                  }
                  i++;
                  if (i == MAX_TRIALS) {
                      JOptionPane.showMessageDialog(UserPanel.this, "Incorrect Password!", "Error", JOptionPane.ERROR_MESSAGE);
                      break;
                  }
              }
              if(option==1) break;//click cancel to exit
          }
        }
      }
    });
    userAddButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
    while(true) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel textLabel = new JLabel("Enter Name");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(textLabel, constraints);

        JTextField textField = new JTextField();
        textField.setColumns(16);
        constraints.gridx = 1;
        panel.add(textField, constraints);


        JLabel passLabel = new JLabel("Enter Password");
        constraints.gridy = 1;
        constraints.gridx = 0;
        panel.add(passLabel, constraints);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setColumns(16);
        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        JLabel confirmField = new JLabel("Confirm Password");
        constraints.gridy = 2;
        constraints.gridx = 0;
        panel.add(confirmField, constraints);

        JPasswordField confirmPassword = new JPasswordField();
        confirmPassword.setColumns(16);
        constraints.gridx = 1;
        panel.add(confirmPassword, constraints);


        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Add User", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
        if (option == 0 ) {
            String s=textField.getText();
            String pass_one = String.valueOf(passwordField.getPassword());
            String pass_two = String.valueOf(confirmPassword.getPassword());
            if (pass_one.equals(pass_two) && (s != null && s.length() > 0)) {
                clientContext.user.addUser(s,pass_one);
                UserPanel.this.getAllUsers(listModel);
                break;
            }
            else{
                if(!pass_one.equals(pass_two)) JOptionPane.showMessageDialog(panel, "Passwords don't match!", "Error", JOptionPane.ERROR_MESSAGE );
                if(s== null || s.length() == 0) JOptionPane.showMessageDialog(panel, "Empty Username!", "Error", JOptionPane.ERROR_MESSAGE );
            }

        }
        if(option==1) break;//click cancel to exit
    }

      }
    });

//    userAddButton.addActionListener(new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        final String s = (String) JOptionPane.showInputDialog(
//            UserPanel.this, "Enter user name:", "Add User", JOptionPane.PLAIN_MESSAGE,
//            null, null, "");
//        if (s != null && s.length() > 0) {
//          //TODO modify 2nd arg
//          clientContext.user.addUser(s,s);
//          UserPanel.this.getAllUsers(listModel);
//        }
//      }
//    });

    userList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          userInfoPanel.setText(clientContext.user.showUserInfo(data));
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
