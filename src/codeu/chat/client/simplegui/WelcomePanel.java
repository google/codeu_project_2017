package codeu.chat.client.simplegui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;
import codeu.chat.client.ChatSimpleGui;

// NOTE: JPanel is serializable, but there is no need to serialize UserPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class WelcomePanel extends JPanel {

  private final ClientContext clientContext;

  public WelcomePanel(ClientContext clientContext) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    initialize();
  }

  private void initialize() {
  
  final JPanel buttonPanel = new JPanel();  
  final GridBagConstraints buttonPanelC = new GridBagConstraints();
  final JButton startButton = new JButton("START");
  buttonPanel.add(startButton);

  buttonPanelC.gridx = 0;
  buttonPanelC.gridy = 0;
  buttonPanelC.gridwidth = 10;
  buttonPanelC.gridheight = 1;
  buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
  buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
 
  this.add(buttonPanel, buttonPanelC);
  
  
   startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ChatSimpleGui.this.linkPage();
      }
   });
  }
}
