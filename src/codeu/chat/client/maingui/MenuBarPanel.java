package codeu.chat.client.maingui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;
import codeu.chat.common.LogoutCallback;

// NOTE: JPanel is serializable, but there is no need to serialize MessagePanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class MenuBarPanel extends JPanel {

  // These objects are modified by the Conversation Panel.

  private final ClientContext clientContext;
  private final LogoutCallback logoutCallback;

  public MenuBarPanel(ClientContext clientContext, LogoutCallback logoutCallback) {
    super();
    this.clientContext = clientContext;
    this.logoutCallback = logoutCallback;
    initialize();
  }

  private void initialize() {
    this.setLayout(new BorderLayout());
    this.setPreferredSize(new Dimension(0,50));
    this.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.BLACK));

    final JPanel userInfoPanel = new JPanel(new GridBagLayout());
    GridBagConstraints userInfoConstrains = new GridBagConstraints();
    JLabel userDisplayNameLabel;
    JLabel userNameLabel;
    
    if (clientContext.user.hasCurrent()) {

      userDisplayNameLabel = new JLabel(clientContext.user.getCurrent().displayName);
      userNameLabel = new JLabel("@" + clientContext.user.getCurrent().name);

      userDisplayNameLabel.setFont(new Font(userDisplayNameLabel.getFont().getName(),
      Font.BOLD,userDisplayNameLabel.getFont().getSize()));

      userNameLabel.setFont(new Font(userNameLabel.getFont().getName(),
      Font.ITALIC,userDisplayNameLabel.getFont().getSize() - 1));

    }else{

      userDisplayNameLabel = new JLabel("User diplay name",SwingConstants.LEFT);
      userNameLabel = new JLabel("@name",SwingConstants.LEFT);

    }

    userInfoConstrains.insets = new Insets(0,5,0,5);
    userInfoConstrains.anchor = GridBagConstraints.WEST;
    userInfoConstrains.fill = GridBagConstraints.NONE;
    userInfoConstrains.weightx = 1.0;
    userInfoConstrains.gridx = 0;
    userInfoConstrains.gridy = 0;

    userInfoPanel.add(userDisplayNameLabel,userInfoConstrains);

    userInfoConstrains.gridx = 0;
    userInfoConstrains.gridy = 1;

    userInfoPanel.add(userNameLabel,userInfoConstrains);

    final JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);

    final JPopupMenu popup = new JPopupMenu();
    
    final JMenuItem logoutLabel = new JMenuItem(new AbstractAction("Logout") {
      public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(null, 
        "Are you sure you wish to logout?",null, JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION) {
          logoutCallback.onLogoutRequest();
        } 
      }
    });

    popup.add(logoutLabel);

    final JLabel settingsButton = new JLabel("Settings");
    toolBar.add(settingsButton);

    this.add(toolBar, BorderLayout.EAST);
    this.add(userInfoPanel, BorderLayout.CENTER);


    settingsButton.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {

        popup.show(e.getComponent(), e.getX(), e.getY());

      }

    });
  }
}
