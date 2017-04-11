package codeu.chat.client.simplegui2;

import codeu.chat.client.ClientContext;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by nora on 4/6/17.
 */
public class ChatMenu {

private final ClientContext clientContext;

    private BorderPane mainWindow;
    private Scene scene;
    private Stage stage;

    //ChatMenu constructor
    public ChatMenu(ClientContext clientContext){
        this.clientContext=clientContext;

        initialize();
        stage = new Stage();
        scene= new Scene(mainWindow);
        stage.setScene(scene);

    }

    //Public method to call in order to display the main chat stage
    public void display(){
        stage.show();
    }

    //initializes the different panels within the grid layout
    private void initialize(){

        mainWindow=new BorderPane();
        mainWindow.setMinHeight(700);
        mainWindow.setMinWidth(700);

        //organize main Pane
        mainWindow.setTop(topMenuBar());
        mainWindow.setLeft(sideBar());
        mainWindow.setBottom(textBar());
        mainWindow.setCenter(centralTextBox());

    }

    private HBox topMenuBar(){

        //create menu at top
        HBox topbar= new HBox(10);
        topbar.setMaxHeight(100);
        topbar.setPadding(new Insets(15, 12, 15, 12));
        topbar.setStyle("-fx-background-color: #336699;");


        //add buttons for different options
        Button signOut = new Button("Sign Out");
        signOut.setPrefSize(100, 20);

        Button addUser = new Button("Add User");
        signOut.setPrefSize(100, 20);

        Button newConvo = new Button("New Conversation");
        signOut.setPrefSize(100, 20);

        //add buttons to menu
        topbar.getChildren().addAll(signOut, addUser, newConvo);

        return topbar;
    }

    private static VBox sideBar(){

        //create sidebar
        VBox sidebar= new VBox(5);

        sidebar.setPadding(new Insets(3));
        sidebar.setAlignment(Pos.TOP_LEFT);
        sidebar.setMinWidth(200);

        //Label for box
        Label sideLabel=new Label("Conversations");

        //border for box
        sidebar.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 4;"  +
                        "-fx-border-insets: 0;"+
                "-fx-border-radius: 5;" +
                "-fx-border-color: #336699;");

        sidebar.getChildren().addAll(sideLabel);

        return sidebar;

    }
    private static HBox textBar(){

        //create menu at bottom
        HBox bottombar=new HBox(10);
        bottombar.setMinHeight(200);

        TextArea textBox=new TextArea();
        textBox.setWrapText(true);

        //border for box
        bottombar.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 4;"  +
                "-fx-border-radius: 5;" +
                "-fx-border-color: #336699;");

        //Button to send messages.
        Button send = new Button("Send");
        send.setPrefSize(100, 20);
        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                //if contains bad word, alert box

            }
        });

        //will be place to input text
        bottombar.getChildren().addAll(textBox, send);

        return bottombar;

    }
    private static VBox centralTextBox(){

        VBox centralBox=new VBox();

        TextArea messages=new TextArea();
        messages.setPrefSize(500,500);
        messages.setEditable(false);
        messages.setWrapText(true);

        centralBox.getChildren().addAll(messages);

        return centralBox;
    }
}
