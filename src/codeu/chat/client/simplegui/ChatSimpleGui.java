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

    // Outermost frame.
    // NOTE: may have to tweak size, or place in scrollable panel.
    mainFrame = new JFrame("ChatU");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(790, 450);

    // Main View - outermost graphics panel.
    final JPanel mainViewPanel = new JPanel(new GridBagLayout());
    //mainViewPanel.setBorder(paneBorder());

    // Build main panels - Sign In, Create Account.

    // Panel with just label -- Cleaner to make a new class?
    final JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
    labelPanel.setBorder(paneBorder());
    labelPanel.add(new JLabel("Welcome to ChatU!"));
    labelPanel.add(new JLabel("Please choose from an option below:"));
    final GridBagConstraints labelViewC = new GridBagConstraints();

    final JPanel userSignIn = new SignInPanel();
    userSignIn.setBorder(paneBorder());
    final GridBagConstraints usersViewC = new GridBagConstraints();

    // ConversationsPanel gets access to MessagesPanel
    final JPanel newAccountPanel = new CreateAccountPanel();
    newAccountPanel.setBorder(paneBorder());
    final GridBagConstraints newAccountViewC = new GridBagConstraints();

    // Dummy panel for formatting
    final JPanel dummyPanel = new JPanel();
    dummyPanel.setBorder(paneBorder());
    final GridBagConstraints DummyViewC = new GridBagConstraints();

    // Placement of main panels.

    labelViewC.gridx = 0;
    labelViewC.gridy = 0;
    labelViewC.gridwidth = 2;
    labelViewC.gridheight = 1;
    labelViewC.fill = GridBagConstraints.BOTH;
    labelViewC.weighty = 0.7;

    usersViewC.gridx = 0;
    usersViewC.gridy = 1;
    usersViewC.gridwidth = 1;
    usersViewC.gridheight = 1;
    usersViewC.fill = GridBagConstraints.BOTH;
    usersViewC.weightx = 0.3;
    usersViewC.weighty = 0.3;

    newAccountViewC.gridx = 1;
    newAccountViewC.gridy = 1;
    newAccountViewC.gridwidth = 1;
    newAccountViewC.gridheight = 1;
    newAccountViewC.fill = GridBagConstraints.BOTH;
    newAccountViewC.weightx = 0.7;
    newAccountViewC.weighty = 0.3;

    DummyViewC.gridx = 0;
    DummyViewC.gridy = 2;
    DummyViewC.gridwidth = 2;
    DummyViewC.gridheight = 1;
    DummyViewC.fill = GridBagConstraints.BOTH;
    DummyViewC.weighty = 0.7;

    mainViewPanel.add(labelPanel, labelViewC);
    mainViewPanel.add(userSignIn, usersViewC);
    mainViewPanel.add(newAccountPanel, newAccountViewC);
    mainViewPanel.add(dummyPanel, DummyViewC);

    mainFrame.add(mainViewPanel);
    mainFrame.pack();
  }
}
