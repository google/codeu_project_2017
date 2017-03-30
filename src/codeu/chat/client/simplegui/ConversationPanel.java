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
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import codeu.chat.client.ClientContext;
import codeu.chat.common.ConversationSummary;

// NOTE: JPanel is serializable, but there is no need to serialize ConversationPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class ConversationPanel extends JPanel {

  private final long POLLING_PERIOD_MS = 1000;
  private final long POLLING_DELAY_MS = 0;
  private final ClientContext clientContext;
  private final MessagePanel messagePanel;
  private ConversationSummary lastConversation;

  public ConversationPanel(ClientContext clientContext, MessagePanel messagePanel) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    this.messagePanel = messagePanel;
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

    final DefaultListModel<String> listModel = new DefaultListModel<>();
    final JList<String> conversationList = new JList<>(listModel);
    conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    conversationList.setVisibleRowCount(15);
    conversationList.setSelectedIndex(-1);

    final JScrollPane listScrollPane = new JScrollPane(conversationList);
    listShowPanel.add(listScrollPane);
    listScrollPane.setMinimumSize(new Dimension(250, 200));

    // Button bar
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();

    final JButton updateButton = new JButton("Update");
    final JButton addButton = new JButton("Add");

    updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonPanel.add(updateButton);
    buttonPanel.add(addButton);

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

    // User clicks Conversations Update button.
    updateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final String selected = conversationList.getSelectedValue();
        ConversationPanel.this.getAllConversations(listModel, true);
        conversationList.setSelectedValue(selected, false);
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
            ConversationPanel.this.getAllConversations(listModel, true);
            conversationList.setSelectedValue(s, true);
          }
        } else {
          JOptionPane.showMessageDialog(ConversationPanel.this, "You are not signed in.");
        }
      }
    });

    // User clicks on Conversation - Set Conversation to current and fill in Messages panel.
    conversationList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (conversationList.getSelectedIndex() != -1) {
          final int index = conversationList.getSelectedIndex();
          final String data = conversationList.getSelectedValue();
          final ConversationSummary cs = ConversationPanel.this.lookupByTitle(data, index);

          clientContext.conversation.setCurrent(cs);

          messagePanel.update(cs);
        }
      }
    });

    getAllConversations(listModel, true);

    // Poll the server for updates
    Timer userUpdateTimer = new Timer();
    userUpdateTimer.schedule(new TimerTask() {
      @Override
      public void run() {

        // Remember what conversation was selected
        final String selected = conversationList.getSelectedValue();

        // Update the conversation display panel
        ConversationPanel.this.getAllConversations(listModel, false);

        // Reselect the conversation
        conversationList.setSelectedValue(selected, false);

      }
    }, POLLING_DELAY_MS, POLLING_PERIOD_MS);
  }

  // Populate ListModel - updates display objects.
  private void getAllConversations(DefaultListModel<String> convDisplayList, boolean replaceAll) {

    // Get all of the conversations
    clientContext.conversation.updateAllConversations(false);

    // If reloading all conversations, the panel should be empty and there is no last conversation displayed
    if (replaceAll) {
      convDisplayList.clear();
      lastConversation = null;
    }

    // The last conversation to be displayed
    ConversationSummary newLast = lastConversation;

    for (final ConversationSummary conv : clientContext.conversation.getConversationSummaries()) {

      // Display the conversation's title if it isn't in the panel yet
      if (replaceAll
              || lastConversation == null
              || (conv.creation.compareTo(lastConversation.creation) >= 0
              && !conv.id.equals(lastConversation.id))) {

        convDisplayList.addElement(conv.title);

        // Remember the most recently displayed conversation
        if (newLast == null || conv.creation.compareTo(newLast.creation) > 0 ) {
          newLast = conv;
        }
      }
    }
    // Store the most recently displayed conversation
    lastConversation = newLast;
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
}
