package codeu.chat.client.simplegui2;

import codeu.chat.client.ClientContext;
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
public final class NewConversation {

    private final ClientContext clientContext;
    private final String title;
    private final Button button;
    private String input;
    private final Stage stage;

    public NewConversation(ClientContext clientContext, String title, String button){

        this.clientContext=clientContext;
        this.title=title;
        this.button=new Button(button);
        this.stage=new Stage();
        this.input="";

        initialize();

    }


    private void initialize(){


        BorderPane grid =new BorderPane();
        VBox userTextInput=new VBox();
        grid.setCenter(userTextInput);

        Scene scene= new Scene(grid);

        //userName text field
        TextArea conversationName=new TextArea();

        //Instruction Label
        Label logInInstruction=new Label(this.title);

        //Sign in button

        this.button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                if(conversationName.getText() == null || conversationName.getText().trim().equals("")){
                    AlertBox alertNoInput= new AlertBox("Empty Conversation Field", "Please enter a new conversation name.");
                    alertNoInput.display();
                }
                else{
                    input = conversationName.getText();
                    stage.close();

                }
            }
        });

        userTextInput.getChildren().addAll(logInInstruction, conversationName, this.button);
        this.stage.setScene(scene);

    }
    public void enterNewConversation(){

        this.stage.showAndWait();

    }

    public String conversationInput(){
        return this.input;
    }


}
