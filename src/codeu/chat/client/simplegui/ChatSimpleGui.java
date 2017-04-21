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
import javax.swing.border.Border;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;


// Chat - top-level client application - Java Simple GUI (using Java Swing)
public final class ChatSimpleGui {

  private final static Logger.Log LOG = Logger.newLog(ChatSimpleGui.class);
  private static User PreviousUsers;
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

    /* modifies look and feel of GUI */
    try{
      UIManager.LookAndFeelInfo[] laf=UIManager.getInstalledLookAndFeels();

      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    }catch(Exception e){
      System.out.println("Problems editing the look and feel");
      System.exit(-1);
    }
    
    // Outermost frame.
    // NOTE: may have tweak size, or place in scrollable panel.
    mainFrame = new JFrame("Chat");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(790, 500);
    mainFrame.setLocation(300, 300);

    /* Adds a menu bar with exit and sign-in options */ 
    JMenuBar menuBar = new JMenuBar();

    JMenu userMenu = new JMenu("Options");
    JMenuItem jmiSwitchUser = new JMenuItem("Manage Users");
    userMenu.add(jmiSwitchUser);
    JMenuItem jmiExit = new JMenuItem("Exit");
    userMenu.add(jmiExit);

    menuBar.add(userMenu);
    mainFrame.setJMenuBar(menuBar);

    /* Creates "manage users" window in advance to maintain current user sign-in */
    JFrame popUpFrame = new JFrame("Manage Users");
    popUpFrame.setSize(400, 400);
    popUpFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    popUpFrame.setLocation(360, 360);

    /* if "manage users" option is clicked, opens up sign-in window */
    jmiSwitchUser.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("Manage Users")) {

          popUpFrame.setVisible(true);

          // Build main panels - Users, Conversations, Messages.
          final JPanel usersViewPanel = new UserPanel(clientContext);
          usersViewPanel.setBorder(paneBorder());
          final GridBagConstraints usersViewC = new GridBagConstraints();

          popUpFrame.getContentPane().add(usersViewPanel);
        }
      }
    });

    /* allows user to close out of chat app */
    jmiExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("Exit"))
             try{
              FileOutputStream file = new FileOutputStream("clientContext.ser");
              ObjectOutputStream outStream = null;
              outStream = new ObjectOutputStream(file);
              outStream.writeObject(clientContext);
              outStream.close();
            }catch(Exception ex){
                System.out.println("Error in writing to file for ClientContext");
            }
          System.exit(0);
      }
    });

    // Main View - outermost graphics panel.
    final JPanel mainViewPanel = new JPanel(new GridBagLayout());
    mainViewPanel.setBorder(paneBorder());

    final MessagePanel messagesViewPanel = new MessagePanel(clientContext);
    messagesViewPanel.setBorder(paneBorder());
    final GridBagConstraints messagesViewC = new GridBagConstraints();

    // ConversationsPanel gets access to MessagesPanel
    final JPanel conversationsViewPanel = new ConversationPanel(clientContext, messagesViewPanel);
    conversationsViewPanel.setBorder(paneBorder());
    final GridBagConstraints conversationViewC = new GridBagConstraints();

    // Placement of main panels.
    conversationViewC.gridx = 0;
    conversationViewC.gridy = 0;
    conversationViewC.gridwidth = 1;
    conversationViewC.gridheight = 1;
    conversationViewC.fill = GridBagConstraints.BOTH;
    conversationViewC.weightx = 0.4;
    conversationViewC.weighty = 0.5;

    messagesViewC.gridx = 1;
    messagesViewC.gridy = 0;
    messagesViewC.gridwidth = 1;
    messagesViewC.gridheight = 1;
    messagesViewC.fill = GridBagConstraints.BOTH;
    messagesViewC.weightx = 0.6;
    messagesViewC.weighty = 0.5;

    mainViewPanel.add(conversationsViewPanel, conversationViewC);
    mainViewPanel.add(messagesViewPanel, messagesViewC);

    mainFrame.add(mainViewPanel);
    mainFrame.pack();
  }

}

