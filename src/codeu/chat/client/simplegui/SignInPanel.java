package codeu.chat.client.simplegui;

/**
 * Created by Suveena on 5/18/17.
 */

import javax.swing.*;
import java.awt.*;

/*import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;*/

@SuppressWarnings("serial")
public final class SignInPanel extends JPanel {

    public SignInPanel() {
        super(new GridBagLayout());
        initialize();
    }

    private void initialize() {

        // This panel contains from top to bottom; a user message, Username field, Password field, and button

        // Set layout within panel
        JPanel InnerLayout = new JPanel();
        InnerLayout.setLayout(new BoxLayout(InnerLayout, BoxLayout.Y_AXIS));

        JLabel userQuestionLabel = new JLabel("Already a user?");
        JLabel userLabel = new JLabel("Username");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password");
        JTextField passwordField = new JTextField();
        JButton signInButton = new JButton("Sign In");

        InnerLayout.add(userQuestionLabel);
        InnerLayout.add(userLabel);
        InnerLayout.add(usernameField);
        InnerLayout.add(passwordLabel);
        InnerLayout.add(passwordField);
        InnerLayout.add(signInButton);

        this.add(InnerLayout);

        /*signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // TODO
            }
        });*/

    }
}

