package codeu.chat.client.simplegui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;
 

// NOTE: JPanel is serializable, but there is no need to serialize UserPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class WelcomePanel extends JPanel {

  private final ClientContext clientContext;
  private final JFrame jFrame;
  private final JPanel jPanel;

  public WelcomePanel(ClientContext clientContext, JFrame jFrame, JPanel jPanel) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    this.jFrame = jFrame;
    this.jPanel = jPanel;
    initialize();
  }
  
  //Initializes button panel
  private void initialize() {
  
  //Creates button panel 
  final JPanel buttonPanel = new JPanel();  
  final GridBagConstraints buttonPanelC = new GridBagConstraints();

  //Creates start button
  final JButton startButton = new JButton("START");
  buttonPanel.add(startButton);
  
  //Parameters of button panel
  buttonPanelC.gridx = 0;
  buttonPanelC.gridy = 0;
  buttonPanelC.gridwidth = 10;
  buttonPanelC.gridheight = 1;
  buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
  buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
 
  this.add(buttonPanel, buttonPanelC);
  
   //Button links to messaging portion of app
   startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
  	 System.out.println("CATS");
         jFrame.getContentPane().removeAll();
         jFrame.getContentPane().add(jPanel);
	//must link to messenger/conversation panels

      }
   });
  } 
}
