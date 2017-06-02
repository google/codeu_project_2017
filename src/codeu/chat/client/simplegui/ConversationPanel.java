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

import codeu.chat.client.ClientContext;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.User;
import codeu.chat.util.Uuid;
import codeu.chat.common.Conversation;


// NOTE: JPanel is serializable, but there is no need to serialize ConversationPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class ConversationPanel extends JPanel {

  private final ClientContext clientContext;
  private final MessagePanel messagePanel;
  private final DefaultListModel<String> convDisplayList = new DefaultListModel<>();

  public ConversationPanel(ClientContext clientContext, MessagePanel messagePanel) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    this.messagePanel = messagePanel;
    //this.userPanel = userPanel;
    initialize();
  }

  private void initialize() {

    // This panel contains from top to bottom: a title bar,
    // a list of conversations, and a button bar.

    // Title
    final JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    final GridBagConstraints titlePanelC = new GridBagConstraints();
    titlePanelC.gridx = 0;
    titlePanelC.gridy = 0;
    titlePanelC.anchor = GridBagConstraints.PAGE_START;

    final JLabel titleLabel = new JLabel("Conversations", JLabel.LEFT);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    titlePanel.add(titleLabel);

    // Conversation list
    final JPanel listShowPanel = new JPanel();
    final GridBagConstraints listPanelC = new GridBagConstraints();

    final DefaultListModel<String> inviteListModel = new DefaultListModel<>();
    final JList<String> objectList = new JList<>(convDisplayList);
    final JList<String> inviteList = new JList<>(inviteListModel);

    objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    objectList.setVisibleRowCount(15);
    objectList.setSelectedIndex(-1);

    inviteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    inviteList.setVisibleRowCount(15);
    inviteList.setSelectedIndex(-1);

    final JScrollPane listScrollPane = new JScrollPane(objectList);
    listShowPanel.add(listScrollPane);
    listScrollPane.setMinimumSize(new Dimension(250, 200));
    listScrollPane.setPreferredSize(new Dimension(250, 200));

    // Button bar
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();
    
    final JButton updateButton = new JButton("Update Conversations");
    final JButton addButton = new JButton("Add");
    final JButton inviteButton = new JButton("Invite Users to Conversations");

    updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonPanel.add(updateButton);
    buttonPanel.add(addButton);
    buttonPanel.add(inviteButton);

    // Put panels together
    titlePanelC.gridx = 0;
    titlePanelC.gridy = 0;
    titlePanelC.gridwidth = 10;
    titlePanelC.gridheight = 4;
    titlePanelC.fill = GridBagConstraints.HORIZONTAL;
    titlePanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    listPanelC.gridx = 0;
    listPanelC.gridy = 4;
    listPanelC.gridwidth = 10;
    listPanelC.gridheight = 4;
    listPanelC.fill = GridBagConstraints.BOTH;
    listPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
    listPanelC.weightx = 0.8;
    listPanelC.weighty = 0.5;

    buttonPanelC.gridx = 0;
    buttonPanelC.gridy = 8;
    buttonPanelC.gridwidth = 10;
    buttonPanelC.gridheight = 4;
    buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
    buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    this.add(titlePanel, titlePanelC);
    this.add(listShowPanel, listPanelC);
    this.add(buttonPanel, buttonPanelC); 
    titlePanel.setBackground(new Color(102, 162, 237));
    listShowPanel.setBackground(new Color(102, 162, 237));
    buttonPanel.setBackground(new Color(102, 162, 237));

    // User clicks Conversations Update button.
    updateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ConversationPanel.this.getAllConversations();
      }
    });

    // User clicks Conversations Add button.
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (clientContext.user.hasCurrent()) {
          final String s = (String) JOptionPane.showInputDialog(
              ConversationPanel.this, "Enter title:", "Add Conversation", JOptionPane.PLAIN_MESSAGE,
              null, null, "");
          if (s != null && s.length() > 0) {
            clientContext.conversation.startConversation(s, clientContext.user.getCurrent().id);
            ConversationPanel.this.getAllConversations();
          }
        } else {
          JOptionPane.showMessageDialog(ConversationPanel.this, "You are not signed in.", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    //user clicks invite button
    inviteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        ConversationPanel.this.getInvitableUsers(inviteListModel);
        if (objectList.getSelectedIndex() != -1 && !inviteListModel.isEmpty()) {
          JPanel p = new JPanel();

          final JScrollPane inviteScrollPane = new JScrollPane(inviteList);
          inviteScrollPane.setMinimumSize(new Dimension(250, 200));
          inviteScrollPane.setPreferredSize(new Dimension(250, 200));

          p.add(inviteScrollPane);
          JOptionPane.showConfirmDialog(null, p, "Add User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

          //if a user is selected from invite list
          if (inviteList.getSelectedIndex() != -1) {
            final String data = inviteList.getSelectedValue(); //select from list
            for (final User u : clientContext.user.getUsers()) { //go through users and see if name selected matches any user in list
              //User names are unique. Checking if selected name matches any of the existing users
              if((u.name).equals(data)){
                Conversation current = clientContext.conversation.getConversation(clientContext.conversation.getCurrentId()); //get current conversation
                if( !current.users.contains(u.id)){
                  clientContext.conversation.joinConversation(u);
                }
                else{
                  JOptionPane.showMessageDialog(ConversationPanel.this, "User already part of conversation", "Error", JOptionPane.ERROR_MESSAGE);
                }
              }
            }
          }

        } else if (inviteListModel.isEmpty()) {
          JOptionPane.showMessageDialog(ConversationPanel.this, "No available users to add", "Error", JOptionPane.ERROR_MESSAGE);

        } else {
          JOptionPane.showMessageDialog(ConversationPanel.this, "No conversations selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

  // User clicks on Conversation - Set Conversation to current and fill in Messages panel.
    objectList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (objectList.getSelectedIndex() != -1) {
          final int index = objectList.getSelectedIndex();
          final String data = objectList.getSelectedValue();
          final ConversationSummary cs = ConversationPanel.this.lookupByTitle(data, index);

          clientContext.conversation.setCurrent(cs);
          messagePanel.update(cs);
        }
      }
    });

    getAllConversations();
  }

  // Populate ListModel - updates display objects.
  public void getAllConversations() {

    clientContext.conversation.updateAllConversations(false);
    convDisplayList.clear();

    for (final ConversationSummary conv : clientContext.conversation.getConversationSummaries()) {
      Conversation c = clientContext.conversation.getConversation(conv.id);
      if (clientContext.user.getCurrent() != null){

        Uuid userID = clientContext.user.getCurrent().id;
        if( c.owner.equals(userID) || c.users.contains(userID)) {
          convDisplayList.addElement(conv.title);
        }
      }
    }
  }

  // Locate the Conversation object for a selected title string.
  // index handles possible duplicate titles.
  private ConversationSummary lookupByTitle(String title, int index) {

    int localIndex = 0;
    for (final ConversationSummary cs : clientContext.conversation.getConversationSummaries()) {
      if ((localIndex >= index) && cs.title.equals(title)) {
        return cs;
      }
      localIndex++;
    }
    return null;
  }

  private void getInvitableUsers(DefaultListModel<String> usersList) {
    clientContext.user.updateUsers();
    usersList.clear();
    Conversation current = clientContext.conversation.getConversation(clientContext.conversation.getCurrentId());
    if(current != null) {
      for (final User u : clientContext.user.getUsers()) {
        if (!(u.name).equals(clientContext.user.getCurrent().name) && !(current.users.contains(u.id))) {
          usersList.addElement(u.name);
        }
      }
    }
  }
}
