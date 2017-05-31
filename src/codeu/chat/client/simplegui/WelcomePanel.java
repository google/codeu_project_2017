package codeu.chat.client.simplegui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
 
// NOTE: JPanel is serializable, but there is no need to serialize UserPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class WelcomePanel extends JPanel {
  
 private JButton startButton;
 
  WelcomePanel() {
    super(new GridBagLayout());
    initialize();
  }
  
  //Initializes button panel
  private void initialize() {
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();
    startButton = new JButton("START");
    buttonPanel.add(startButton);

    buttonPanelC.gridx = 0;
    buttonPanelC.gridy = 0;
    buttonPanelC.gridwidth = 10;
    buttonPanelC.gridheight = 1;
    buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
    buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    this.add(buttonPanel, buttonPanelC);
    startButton.addActionListener(ae -> System.out.println("Hello world"));
   	 
  } 
  
  public JButton getStartButton() {
    return startButton;
  }

}
