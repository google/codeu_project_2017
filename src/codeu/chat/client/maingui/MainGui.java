package codeu.chat.client.maingui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;
import codeu.chat.common.LoginInputCallback;
import codeu.chat.common.User;

// Chat - top-level client application - Java Simple GUI (using Java Swing)
public final class MainGui implements LoginInputCallback{

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
    final JPanel loginViewPanel = new LoginPanel(clientContext,this);

    // Show login if user not connected
    mainViewPanel.add(loginViewPanel,BorderLayout.CENTER);

    mainFrame.add(mainViewPanel);
    mainFrame.pack();
  }

  // Login request callback function 
  @Override
  public void onLoginRequest(String username, String pswd){
    
    // Search for user in the server's database
    final User loginUser = clientContext.user.checkUserInDatabase(username, pswd);

    if(loginUser != null){

      // Check if user is already logged
      if(clientContext.user.getCurrent() == null){

        // Sign in with the returned user from the database
        if(clientContext.user.signInUser(loginUser)){

          // User successfully signed in
          JOptionPane.showMessageDialog(mainFrame,"Hello " + clientContext.user.getCurrent().display_name);
          mainFrame.getContentPane().removeAll();
        }
      }else{

        // User is already logged
        JOptionPane.showMessageDialog(mainFrame, "User is already logged");
      }
    }else{

      // User not found in database or incorrect password
      System.out.println("User not found or incorrect password");
    }
  }
}
