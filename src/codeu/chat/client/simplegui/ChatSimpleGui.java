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
import javax.swing.*;
import javax.swing.border.Border;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;

// Chat - top-level client application - Java Simple GUI (using Java Swing)
public final class ChatSimpleGui {

  private final static Logger.Log LOG = Logger.newLog(ChatSimpleGui.class);

  private JFrame mainFrame;

  private final ClientContext clientContext;

  // Constructor - sets up the Chat Application
  public ChatSimpleGui(Controller controller, View view) {
    clientContext = new ClientContext(controller, view);
  }

  // Run the GUI client
  public void run() {
    try {
      initialize();
      mainFrame.setVisible(true);
    } catch (Exception ex) {
      System.out.println("ERROR: Exception in ChatSimpleGui.run. Check log for details.");
      LOG.error(ex, "Exception in ChatSimpleGui.run");
      System.exit(1);
    }
  }

  private Border paneBorder() {
    Border outside = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
    Border inside = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    return BorderFactory.createCompoundBorder(outside, inside);
  }
  
  // Initialize the GUI
  private void initialize() {
     
    // NOTE: may have tweak size, or place in scrollable panel.
    mainFrame = new JFrame("Chat");
    mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    mainFrame.setSize(790, 450);

    // Main View - outermost graphics panel.
    final JPanel mainViewPanel = new JPanel(new GridBagLayout());
    mainViewPanel.setBorder(paneBorder());

    // Welcome Panel, Outermost Frame
    final WelcomePanel welcomeViewPanel = new WelcomePanel();
    welcomeViewPanel.setBorder(paneBorder());
    final GridBagConstraints welcomeViewC = new GridBagConstraints();

    // Build main panels - Users, Conversations, Messages.
    final JPanel usersViewPanel = new UserPanel(clientContext);
    usersViewPanel.setBorder(paneBorder());
    final GridBagConstraints usersViewC = new GridBagConstraints();

    final MessagePanel messagesViewPanel = new MessagePanel(clientContext);
    messagesViewPanel.setBorder(paneBorder());
    final GridBagConstraints messagesViewC = new GridBagConstraints();

    // ConversationsPanel gets access to MessagesPanel
    final JPanel conversationsViewPanel = new ConversationPanel(clientContext, messagesViewPanel);
    conversationsViewPanel.setBorder(paneBorder());
    final GridBagConstraints conversationViewC = new GridBagConstraints();

    // Placement of main panels.

    //User Panel Parameters
    usersViewC.gridx = 0;
    usersViewC.gridy = 0;
    usersViewC.gridwidth = 1;
    usersViewC.gridheight = 1;
    usersViewC.fill = GridBagConstraints.BOTH;
    usersViewC.weightx = 0.3;
    usersViewC.weighty = 0.3;

    //Conversation Panel Parameters
    conversationViewC.gridx = 1;
    conversationViewC.gridy = 0;
    conversationViewC.gridwidth = 1;
    conversationViewC.gridheight = 1;
    conversationViewC.fill = GridBagConstraints.BOTH;
    conversationViewC.weightx = 0.7;
    conversationViewC.weighty = 0.3;

    //Message Panel Parameters
    messagesViewC.gridx = 0;
    messagesViewC.gridy = 1;
    messagesViewC.gridwidth = 2;
    messagesViewC.gridheight = 1;
    messagesViewC.fill = GridBagConstraints.BOTH;
    messagesViewC.weighty = 0.7;
    
    //Main View Panel Parameters
    mainViewPanel.add(usersViewPanel, usersViewC);
    mainViewPanel.add(conversationsViewPanel, conversationViewC);
    mainViewPanel.add(messagesViewPanel, messagesViewC);
 
    welcomeViewPanel.getStartButton().addActionListener(ae -> {
	mainFrame.remove(welcomeViewPanel);
	mainFrame.add(mainViewPanel);
	mainFrame.pack();

    });

    mainFrame.add(welcomeViewPanel);
    mainFrame.pack();
  }
}
