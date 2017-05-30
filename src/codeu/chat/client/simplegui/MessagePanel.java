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

import codeu.chat.client.ClientContext;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.User;

// NOTE: JPanel is serializable, but there is no need to serialize MessagePanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class MessagePanel extends JPanel {

  // These objects are modified by the Conversation Panel.
  private final JLabel messageOwnerLabel = new JLabel("Owner:", JLabel.RIGHT);
  private final JLabel messageConversationLabel = new JLabel("Conversation:", JLabel.LEFT);
  private final DefaultListModel<String> messageListModel = new DefaultListModel<>();

  private final ClientContext clientContext;

  public MessagePanel(ClientContext clientContext) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    initialize();
  }

  // External agent calls this to trigger an update of this panel's contents.
  public void update(ConversationSummary owningConversation) {

    final User u = (owningConversation == null) ?
        null :
        clientContext.user.lookup(owningConversation.owner);

    messageOwnerLabel.setText("Owner: " +
        ((u==null) ?
            ((owningConversation==null) ? "" : owningConversation.owner) :
            u.name));

    messageConversationLabel.setText("Conversation: " + owningConversation.title);

    getAllMessages(owningConversation);
  }

  private void initialize() {

    // This panel contains the messages in the current conversation.
    // It has a title bar with the current conversation and owner,
    // then a list panel with the messages, then a button bar.

    // Title bar - current conversation and owner
    final JPanel titlePanel = new JPanel(new GridBagLayout());
    final GridBagConstraints titlePanelC = new GridBagConstraints();

    final JPanel titleConvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    final GridBagConstraints titleConvPanelC = new GridBagConstraints();
    titleConvPanelC.gridx = 0;
    titleConvPanelC.gridy = 0;
    titleConvPanelC.anchor = GridBagConstraints.PAGE_START;

    final JPanel titleOwnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    final GridBagConstraints titleOwnerPanelC = new GridBagConstraints();
    titleOwnerPanelC.gridx = 0;
    titleOwnerPanelC.gridy = 1;
    titleOwnerPanelC.anchor = GridBagConstraints.PAGE_START;

    // messageConversationLabel is an instance variable of Conversation panel
    // can update it.
    messageConversationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    titleConvPanel.add(messageConversationLabel);

    // messageOwnerLabel is an instance variable of Conversation panel
    // can update it.
    messageOwnerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    titleOwnerPanel.add(messageOwnerLabel);

    titlePanel.add(titleConvPanel, titleConvPanelC);
    titlePanel.add(titleOwnerPanel, titleOwnerPanelC);
    titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // User List panel.
    final JPanel listShowPanel = new JPanel();
    final GridBagConstraints listPanelC = new GridBagConstraints();

    // messageListModel is an instance variable so Conversation panel
    // can update it.
    final JList<String> userList = new JList<>(messageListModel);
    userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userList.setVisibleRowCount(15);
    userList.setSelectedIndex(-1);

    final JScrollPane userListScrollPane = new JScrollPane(userList);
    listShowPanel.add(userListScrollPane);
    userListScrollPane.setMinimumSize(new Dimension(500, 200));
    userListScrollPane.setPreferredSize(new Dimension(500, 200));

    // Button panel
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();

    final JButton addButton = new JButton("Add");
    buttonPanel.add(addButton);

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

    buttonPanelC.gridx = 0;
    buttonPanelC.gridy = 11;
    buttonPanelC.gridwidth = 10;
    buttonPanelC.gridheight = 1;
    buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
    buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    this.add(titlePanel, titlePanelC);
    this.add(listShowPanel, listPanelC);
    this.add(buttonPanel, buttonPanelC);

    // User click Messages Add button - prompt for message body and add new Message to Conversation
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!clientContext.user.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.");
        } else if (!clientContext.conversation.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.");
        } else {
          final String messageText = (String) JOptionPane.showInputDialog(
              MessagePanel.this, "Enter message:", "Add Message", JOptionPane.PLAIN_MESSAGE,
              null, null, "");
          if (messageText != null && messageText.length() > 0) {
            clientContext.message.addMessage(
                clientContext.user.getCurrent().id,
                clientContext.conversation.getCurrentId(),
                messageText);
            MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent());
          }
        }
      }
    });

    // Panel is set up. If there is a current conversation, Populate the conversation list.
    getAllMessages(clientContext.conversation.getCurrent());
  }

  // Populate ListModel
  // TODO: don't refetch messages if current conversation not changed
  private void getAllMessages(ConversationSummary conversation) {
    messageListModel.clear();

    for (final Message m : clientContext.message.getConversationContents(conversation)) {
      // Display author name if available.  Otherwise display the author UUID.
      final String authorName = clientContext.user.getName(m.author);

      final String displayString = String.format("%s: [%s]: %s",
          ((authorName == null) ? m.author : authorName), m.creation, m.content);

      messageListModel.addElement(displayString);
    }
  }
}
