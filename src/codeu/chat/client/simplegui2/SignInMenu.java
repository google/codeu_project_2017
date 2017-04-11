package codeu.chat.client.simplegui2;

import codeu.chat.client.View;
import codeu.chat.client.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Created by nora on 4/9/17.
 */
public final class SignInMenu {



    public static Scene signIn(){

        BorderPane signInWindow= new BorderPane();
        signInWindow.setCenter(signInContent());
        Scene window1=new Scene(signInWindow);

        return window1;

    }

    private static VBox signInContent(){

        VBox newUser=new VBox();

        //userName text field
        TextArea user=new TextArea();



        //Instruction Label
        Label logInInstruction=new Label("Enter your username:");

        //Sign in button
        Button signInButton=new Button("Sign in");
        signInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(user.getText() == null || user.getText().trim().equals("")){
                    AlertBox alertNoInput= new AlertBox();
                    alertNoInput.display("Empty Username Field", "Please enter a valid username.");
                }
                else{

                }
            }
        });

        newUser.getChildren().addAll(logInInstruction, user, signInButton);


        return newUser;

    }
}
