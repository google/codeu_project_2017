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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.TimerTask;
import javax.swing.*;
import java.util.Timer;

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

  private final long POLLING_PERIOD_MS = 1000;
  private final long POLLING_DELAY_MS = 0;
  private final ClientContext clientContext;
  private Message lastMessage;

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
        ((u == null) ?
            ((owningConversation == null) ? "" : owningConversation.owner) :
              u.name));

    messageConversationLabel.setText("Conversation: " +
        (owningConversation == null ? "" : owningConversation.title));

    getAllMessages(owningConversation, true);
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
    final JList<String> messageList = new JList<>(messageListModel);
    messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    messageList.setVisibleRowCount(15);
    messageList.setSelectedIndex(-1);

    final JScrollPane userListScrollPane = new JScrollPane(messageList);
    listShowPanel.add(userListScrollPane);
    userListScrollPane.setMinimumSize(new Dimension(500, 200));
    userListScrollPane.setPreferredSize(new Dimension(500, 200));

    // Button panel
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();

    /* area for user to type in messages */
    final JTextField messageField = new JTextField(60);
    buttonPanel.add(messageField);
    messageField.setEditable(true);

    /* allows user to hit enter key to submit a chat message*/
    messageField.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e){
        if(e.getKeyChar() == KeyEvent.VK_ENTER){

          if (!clientContext.user.hasCurrent()) {
            JOptionPane.showMessageDialog(MessagePanel.this, "You are not signed in.");
          }

          else if (!clientContext.conversation.hasCurrent()) {
            JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.");
          }

          else {
            if (messageField.getText() != null && messageField.getText().length() > 0) {
              clientContext.message.addMessage(
                  clientContext.user.getCurrent().id,
                  clientContext.conversation.getCurrentId(),
                  messageField.getText());
              MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent(), true);
              messageField.setText("");

              // scrolls to bottom of messages panel
              JScrollBar verticalScroll = userListScrollPane.getVerticalScrollBar();
              verticalScroll.setValue(verticalScroll.getMaximum());
            }
          }

        }       
      }

      /* mandatory functions to include, left empty */
      public void keyTyped(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {

      }

    });

    final JButton addButton = new JButton("Send");
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
        }

        else if (!clientContext.conversation.hasCurrent()) {
          JOptionPane.showMessageDialog(MessagePanel.this, "You must select a conversation.");
        }

        else {
          if (messageField.getText() != null && messageField.getText().length() > 0) {
            clientContext.message.addMessage(
                clientContext.user.getCurrent().id,
                clientContext.conversation.getCurrentId(),
                messageField.getText());
            MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent(), true);
            messageField.setText("");

            // scrolls to bottom of messages panel
            JScrollBar verticalScroll = userListScrollPane.getVerticalScrollBar();
            verticalScroll.setValue(verticalScroll.getMaximum());
          }
        }
      }
    });

    // Panel is set up. If there is a current conversation, Populate the conversation list.
    getAllMessages(clientContext.conversation.getCurrent(), true);

    // Poll the server for updates
    Timer messageUpdateTimer = new Timer();
    messageUpdateTimer.schedule(new TimerTask() {
      @Override
      public void run() {

        // Remember what message is selected
        final String selected = messageList.getSelectedValue();

        // Get new messages
        clientContext.message.updateMessages(false);

        // Update the message display panel
        MessagePanel.this.getAllMessages(clientContext.conversation.getCurrent(), false);

        // Reselect the message
        messageList.setSelectedValue(selected, false);

      }
    }, POLLING_DELAY_MS, POLLING_PERIOD_MS);
  }

  // Populate ListModel
  private void getAllMessages(ConversationSummary conversation, boolean replaceAll) {

    // If reloading all messages, the panel should be empty and there is no last message displayed
    if (replaceAll) {
      messageListModel.clear();
      lastMessage = null;
    }

    // The most recent message that has been displayed
    Message newLast = lastMessage;

    for (final Message m : clientContext.message.getConversationContents(conversation)) {

      // Display the message if it is not in the panel yet.
      if (replaceAll
          || lastMessage == null
          || (m.creation.compareTo(lastMessage.creation) >= 0
          && !m.id.equals(lastMessage.id))
          ) {

        // Display author name if available.  Otherwise display the author UUID.
        final String authorName = clientContext.user.getName(m.author);

        // Display message in the format Author: [Date Time]: Content
        final String displayString = String.format("%s: [%s]: %s",
            ((authorName == null) ? m.author : authorName), m.creation, m.content);

        messageListModel.addElement(displayString);

        // Remember the most recently displayed message
        if (newLast == null || m.creation.compareTo(newLast.creation) > 0) {
          newLast = m;
        }
      }
    }
    // Store the most recent message
    lastMessage = newLast;
  }
}