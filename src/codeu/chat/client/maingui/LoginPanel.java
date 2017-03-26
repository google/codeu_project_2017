package codeu.chat.client.maingui;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

import codeu.chat.client.ClientContext;
import codeu.chat.common.LoginInputCallback;

@SuppressWarnings("serial")
public final class LoginPanel extends JPanel{

    private final ClientContext clientContext;
    private final LoginInputCallback loginInputCallback;

    public LoginPanel(ClientContext clientContext, LoginInputCallback loginInputCallback) {
        super();
        this.clientContext = clientContext;
        this.loginInputCallback = loginInputCallback;
        initialize();
    }
    private void initialize() {

        // This panel contains the login form for get access to the chat.

        // Configure the size of the panel and the main structure.
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(400,500));
        GridBagConstraints panelC = new GridBagConstraints();
        
        // Username input panel.
        final JPanel usernameInputPanel = new JPanel();
        final TextField usernameTextField = new TextField(20);
        final Label usernameLabel = new Label("Name: ");
        usernameInputPanel.setLayout(new GridBagLayout());
        usernameInputPanel.add(usernameLabel);
        usernameInputPanel.add(usernameTextField);

        // Password input panel
        final JPanel passwordInputPanel = new JPanel();
        final TextField passwordTextField = new TextField(20);
        final Label passwordLabel = new Label("Pswd: ");
        passwordTextField.setEchoChar('*');
        passwordInputPanel.setLayout(new GridBagLayout());
        passwordInputPanel.add(passwordLabel);
        passwordInputPanel.add(passwordTextField);

        // Login button
        final Button loginButton = new Button("Login");

        // Add fields to main panel
        panelC.insets = new Insets(10, 10, 10, 10);
        panelC.gridx = 0;
        panelC.gridy = 1;
        this.add(usernameInputPanel,panelC);
        panelC.gridx = 0;
        panelC.gridy = 2;
        this.add(passwordInputPanel,panelC);        
        panelC.gridx = 0;
        panelC.gridy = 3;
        this.add(loginButton,panelC);

        loginButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                // Login callback
                loginInputCallback.onLoginRequest(usernameTextField.getText(), passwordTextField.getText());
            }
        });
    }
}