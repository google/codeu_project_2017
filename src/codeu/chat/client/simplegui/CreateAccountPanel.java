package codeu.chat.client.simplegui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Suveena on 5/18/17.
 */

@SuppressWarnings("serial")
public class CreateAccountPanel extends JPanel{

    public CreateAccountPanel() {
        super(new GridBagLayout());
        initialize();
    }

    private void initialize() {

        // This panel contains from top to bottom; a message, a button.

        // Set layout within panel
        JPanel InnerLayout = new JPanel();
        InnerLayout.setLayout(new BoxLayout(InnerLayout, BoxLayout.Y_AXIS));

        JLabel userQuestionLabel = new JLabel("Create an Account");
        JButton createAccountButton = new JButton("Get Started!");

        InnerLayout.add(userQuestionLabel);
        InnerLayout.add(createAccountButton);

        this.add(InnerLayout);

        /*signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // TODO
            }
        });*/

    }
}
