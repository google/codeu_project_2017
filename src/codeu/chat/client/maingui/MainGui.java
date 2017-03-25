package codeu.chat.client.maingui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;

// Chat - top-level client application - Java Simple GUI (using Java Swing)
public final class MainGui {

  private final static Logger.Log LOG = Logger.newLog(MainGui.class);

  private JFrame mainFrame;

  private final ClientContext clientContext;

  // Constructor - sets up the Chat Application
  public MainGui(Controller controller, View view) {
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
    // NOTE: may have tweak size, or place in scrollable panel.
    mainFrame = new JFrame("Chat");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(790, 450);
    //mainFrame.setPreferredSize(new Dimension(790,450));

    // Main View - outermost graphics panel.
    final JPanel mainViewPanel = new JPanel(new BorderLayout());

    // Build login panel
    final JPanel loginViewPanel = new LoginPanel(clientContext);

    /*// Build main panels - Users, Conversations, Messages.
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
    usersViewC.gridx = 0;
    usersViewC.gridy = 0;
    usersViewC.gridwidth = 1;
    usersViewC.gridheight = 1;
    usersViewC.fill = GridBagConstraints.BOTH;
    usersViewC.weightx = 0.3;
    usersViewC.weighty = 0.3;

    conversationViewC.gridx = 1;
    conversationViewC.gridy = 0;
    conversationViewC.gridwidth = 1;
    conversationViewC.gridheight = 1;
    conversationViewC.fill = GridBagConstraints.BOTH;
    conversationViewC.weightx = 0.7;
    conversationViewC.weighty = 0.3;

    messagesViewC.gridx = 0;
    messagesViewC.gridy = 1;
    messagesViewC.gridwidth = 2;
    messagesViewC.gridheight = 1;
    messagesViewC.fill = GridBagConstraints.BOTH;
    messagesViewC.weighty = 0.7;

    mainViewPanel.add(usersViewPanel, usersViewC);
    mainViewPanel.add(conversationsViewPanel, conversationViewC);
    mainViewPanel.add(messagesViewPanel, messagesViewC);*/

    mainViewPanel.add(loginViewPanel,BorderLayout.CENTER);

    mainFrame.add(mainViewPanel);
    mainFrame.pack();
  }
}
