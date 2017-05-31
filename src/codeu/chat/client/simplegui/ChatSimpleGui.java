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
import javax.swing.JFrame; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    Border outside = BorderFactory.createLineBorder(Color.white);
    Border inside = BorderFactory.createEmptyBorder(8, 8, 8, 8);
    return BorderFactory.createCompoundBorder(outside, inside);
  }

  // Initialize the GUI
  private void initialize() {

    // Outermost frame.
    // NOTE: may have tweak size, or place in scrollable panel.
    mainFrame = new JFrame("Chat With Friends");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(790, 450);
    mainFrame.setJMenuBar(menuBar());
    
    // Main View - outermost graphics panel
    final JPanel mainViewPanel = new JPanel(new GridBagLayout());
    
    mainViewPanel.setBorder(paneBorder()); 

    // Build main panels - Users, Conversations, Messages
    final JPanel usersViewPanel = new UserPanel(clientContext);

    usersViewPanel.setBorder(paneBorder());
    usersViewPanel.setBackground(new Color(102, 162, 237)); 

    final GridBagConstraints usersViewC = new GridBagConstraints();

    final MessagePanel messagesViewPanel = new MessagePanel(clientContext);
    messagesViewPanel.setBorder(paneBorder());
    messagesViewPanel.setBackground(new Color(102, 162, 237));
    final GridBagConstraints messagesViewC = new GridBagConstraints();

    // ConversationsPanel gets access to MessagesPanel
    final JPanel conversationsViewPanel = new ConversationPanel(clientContext, messagesViewPanel);
    conversationsViewPanel.setBorder(paneBorder());
    conversationsViewPanel.setBackground(new Color(102, 162, 237));
    final GridBagConstraints conversationViewC = new GridBagConstraints();

    // Placement of main panels and search bar.
    usersViewC.gridx = 0;
    usersViewC.gridy = 0;
    usersViewC.gridwidth = 1;
    usersViewC.gridheight = 1;
    usersViewC.fill = GridBagConstraints.BOTH;
    usersViewC.weightx = 0.2;
    usersViewC.weighty = 0.1;

    conversationViewC.gridx = 0;
    conversationViewC.gridy = 1;
    conversationViewC.gridwidth = 1;
    conversationViewC.gridheight = 1;
    conversationViewC.fill = GridBagConstraints.BOTH;
    conversationViewC.weightx = 0.2;
    conversationViewC.weighty = 0.7;

    messagesViewC.gridx = 1;
    messagesViewC.gridy = 0;
    messagesViewC.gridwidth = 2;
    messagesViewC.gridheight = 2;
    messagesViewC.fill = GridBagConstraints.BOTH;
    messagesViewC.weighty = 0.7;
    messagesViewC.weightx = 0.7;

    mainViewPanel.add(usersViewPanel, usersViewC);
    mainViewPanel.add(conversationsViewPanel, conversationViewC);
    mainViewPanel.add(messagesViewPanel, messagesViewC);
    mainViewPanel.setBackground(Color.white); //I just changed

    mainFrame.add(mainViewPanel);
    mainFrame.pack();
  }
  
  //Creates and Builds the Menu Bar
  public static JMenuBar menuBar(){
    JMenuBar topMenuBar = new JMenuBar(); //the menu bar
	JMenu topMenuGeneral = new JMenu("General"); //first category of menu
	final JMenuItem topMenuItem = new JMenuItem("How to Use"); //details how to use the app
	final JMenuItem coders = new JMenuItem("Coders"); //coders on this app or equivalent to credits
	  
	//adds the How to Use information at the top of this menu bar
	topMenuBar.add(topMenuGeneral);
	topMenuBar.add(topMenuItem);
	  
	//constructs the topMenuGeneral drop down menu with submenus 
	topMenuGeneral.add(coders); 
	  
	//topMenuBar.add(topMenuColorSelector);
	  
	topMenuItem.addActionListener(new ActionListener(){
	  @Override 
	  public void actionPerformed(ActionEvent e) {
	  
	  //message on how to use the application
	  final String howToUseMessage = "To use this application, you must first add a user. "
	    							+ "Then, you must sign in the user and add a conversation. "
									+ "Then, you may begin chatting!\n\n"
									+ "To add a user:\n"
									+"1) Click the Add button\n"
									+"2) Enter the user's name\n"
									+"3) Click OK or press ENTER\n\n"
									+"To sign in a user:\n"
									+"1) Click on the username until it is highlighted.\n"
									+"2) Click on the Sign In button.\n\n"
									+"To add a conversation:\n" 
									+"1) Click the Add button\n"
									+"2) Enter the title of the conversation\n"
									+"3) Click OK or press ENTER\n\n"
									+"To switch conversations:\n"
									+"1) Click on the title of the conversation to which you would like to switch and press Choose Conversation.\n\n"
		     						+"To send a message:\n"
			 						+"1) Type your message in the white text editor to the left of the Send Message button.\n"
			    					+"2) Once you are done typing your message, either press the Send Message button, press ENTER, or press RETURN."; 
			  	
      JOptionPane.showMessageDialog(topMenuItem, howToUseMessage, "How to Use", JOptionPane.PLAIN_MESSAGE);
	}
  });
	  
  coders.addActionListener(new ActionListener(){
    @Override 
	public void actionPerformed(ActionEvent e) {
	  JOptionPane.showMessageDialog(topMenuItem, "Created by Google, Mathangi Ganesh, Jess Abramson, and Sarah Depew", "Coders", JOptionPane.PLAIN_MESSAGE);
	}
  });
  
  //return the constructed JMenuBar
  return topMenuBar;
   
  }
}
