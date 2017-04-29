package codeu.chat.client.maingui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;
import codeu.chat.common.LoginInputCallback;
import codeu.chat.common.LogoutCallback;
import codeu.chat.common.User;

// Chat - top-level client application - Java Simple GUI (using Java Swing)
public final class MainGui implements LoginInputCallback, LogoutCallback{

  private final static Logger.Log LOG = Logger.newLog(MainGui.class);

  private JFrame mainFrame;
  private JPanel mainViewPanel;

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

     // Main View - outermost graphics panel.
    mainViewPanel = new JPanel(new BorderLayout());

    // Check if user is logged in order to open the LoginPanel or the other panels.
    if(!clientContext.user.hasCurrent()){
      loadLoginPanel();
    }else{
      loadChatPanels();
    }

    mainFrame.add(mainViewPanel);
    mainFrame.pack();
  }
  public void loadLoginPanel(){
    final LoginPanel loginViewPanel = new LoginPanel(clientContext,this);

    mainViewPanel.add(loginViewPanel,BorderLayout.CENTER);
    mainFrame.pack();
  }
  
  public void loadChatPanels(){
    final MessagePanel messageViewPanel = new MessagePanel(clientContext);
    final ConversationPanel conversationsPanel = new ConversationPanel(clientContext, messageViewPanel);
    final MenuBarPanel menuBarPanel = new MenuBarPanel(clientContext,this);

    mainViewPanel.add(messageViewPanel,BorderLayout.CENTER);
    mainViewPanel.add(conversationsPanel,BorderLayout.WEST);
    mainViewPanel.add(menuBarPanel,BorderLayout.NORTH);
    mainFrame.pack();
    
  }
  // Login request callback function 
  @Override
  public void onLoginRequest(String username, String pswd){
    
    // Search for user in the server's database
    final User loginUser = clientContext.user.checkUserInDatabase(username, pswd);

    if (loginUser != null) {

      // Check if user is already logged
      if (!clientContext.user.hasCurrent()) {

        // Sign in with the returned user from the database
        if (clientContext.user.signInUser(loginUser)) {

          // User successfully signed in
          JOptionPane.showMessageDialog(mainFrame,"Hello " + clientContext.user.getCurrent().displayName);
          mainViewPanel.removeAll();
          loadChatPanels();
        }
      }else{

        JOptionPane.showMessageDialog(mainFrame, "User is already logged in");
      }
    }else{

      System.out.println("User not found or incorrect password");
    }
  }

  @Override
  public void onLogoutRequest(){
    if (clientContext.user.signOutUser()) {
      clientContext.conversation.setCurrent(null);
      mainViewPanel.removeAll();
      loadLoginPanel();
    }else{
      JOptionPane.showMessageDialog(mainFrame,"Error in logout");
    }
  }

}
