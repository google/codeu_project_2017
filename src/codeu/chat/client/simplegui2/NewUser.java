package codeu.chat.client.simplegui2;

import codeu.chat.client.ClientContext;
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
import javafx.stage.Stage;

/**
 * Created by nora on 4/9/17.
 */
public final class NewUser {

    private final ClientContext clientContext;
    private final String input;
    private Stage stage;
    private Scene scene;
    private BorderPane grid;
    private VBox userTextInput;

    public NewUser(ClientContext clientContext){

        this.clientContext=clientContext;
        this.input="";
        initialize();

    }



    private void initialize(){


        this.grid=new BorderPane();
        signInBox();
        grid.setCenter(this.userTextInput);
        this.scene= new Scene(this.grid);
        this.stage=new Stage();
        this.stage.setScene(this.scene);

        this.stage.show();

    }

    private void signInBox(){

        userTextInput=new VBox();

        //userName text field
        TextArea user=new TextArea();



        //Instruction Label
        Label logInInstruction=new Label("Enter your username:");

        //Sign in button
        Button signInButton=new Button("Sign in");
        Button close= new Button("Exit.");

        signInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(user.getText() == null || user.getText().trim().equals("")){
                    AlertBox alertNoInput= new AlertBox("Empty Username Field", "Please enter a valid username.");
                    alertNoInput.display();
                }
                else{
                    clientContext.user.addUser(user.getText());
                    stage.close();
                }
            }
        });

        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });

        userTextInput.getChildren().addAll(logInInstruction, user, signInButton, close);

    }
}
