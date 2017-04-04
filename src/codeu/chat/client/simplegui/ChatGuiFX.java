package codeu.chat.client.simplegui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.*;
import javafx.collections.*;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;

public final class ChatGuiFX extends Application {

    private final static Logger.Log LOG = Logger.newLog(ChatGuiFX.class);


    private static final double WIDTH = 1000;
    private static final double HEIGHT = 500;

    private Stage thestage;                         // Holds the scene that user is currently viewing
    private Scene signInScene, mainScene;           // Scenes to hold all the elements for each page
    private BorderPane signInPane;                  // Root panes for both pages (panes provide structure to scene elements)
    private FlowPane signInLabelPane;               // Panes to hold labels (so that they can be aligned in the root pane)
    private GridPane inputPane;                     // Pane to position username/password input fields and labels, as well as sign-in button
    private Label signInLabel;                    // Labels for the main page and sign in page
    private Button signInButton;                    // Buttons to switch between main page and sign in page
    private TextField userInput, passInput;         // Text fields for entering username and password
    private ClientContext clientContext;            //

    public void run(String [] args) {
        try {
            // launches gui
            Application.launch(ChatGuiFX.class, args);
        } catch (Exception ex) {
            System.out.println("ERROR: Exception in ChatGuiFX.run. Check log for details.");
            LOG.error(ex, "Exception in ChatGuiFX.run");
            System.exit(1);
        }

    }

    public void launch(Controller controller, View view) {
        this.clientContext = new ClientContext(controller, view);
        Application.launch(ChatGuiFX.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Sign in page

        this.thestage = primaryStage;            // Initialize the main stage

        signInPane = new BorderPane();                      // Initialize panes
        signInLabelPane = new FlowPane();
        signInLabelPane.setAlignment(Pos.BOTTOM_CENTER);       // Make sure label will be in center

        signInLabel = new Label("Sign-in screen");      // Initialize label
        signInLabel.setFont(Font.font(20));                 // Set style

        signInButton = new Button("Sign in");                      // Initialize sign in button
        signInButton.setOnAction((event)-> ButtonClicked(event));       // Initialize its event handler


        userInput = new TextField("Username");
        passInput = new TextField("Password");

        signInLabelPane.getChildren().add(signInLabel);         // Add labels to their respective panes

        signInPane.setTop(signInLabelPane);
        signInPane.setCenter(signInButton);                 // Add buttons and labels to panes


        signInScene = new Scene(signInPane, WIDTH, HEIGHT);



        // Main Page

        HBox hboxClient = new HBox();
        HBox hboxInput = new HBox();
        VBox userVBox = new VBox();
        VBox chatVBox = new VBox();
        VBox convosVBox = new VBox();
        BorderPane container = new BorderPane();
        Button send = new Button("Send");
        Button update = new Button("Update");
        Button addConvo = new Button("Add Conversation");
        Text userTitle = new Text("Users");
        Text chatTitle = new Text("Conversation"); // changed based on name?
        Text convosTitle = new Text("Conversations");
        TextFlow userTf = new TextFlow(userTitle);
        TextFlow chatTf = new TextFlow(chatTitle);
        TextFlow convosTf = new TextFlow(convosTitle);
        TextField input = new TextField();
        // MenuBar menuBar = new MenuBar();
        // Menu menuAdd = new Menu("Add Conversation");
        // menuBar.getMenus().addAll(menuConvo);

        ObservableList<String> usersList = FXCollections.observableArrayList(
                 "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
        ListView<String> users = new ListView<String>(usersList);
        ObservableList<String> convoList = FXCollections.observableArrayList(
                 "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
        ListView<String> conversations = new ListView<String>(convoList);
        ObservableList<String> messageList = FXCollections.observableArrayList(
                 "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
        ListView<String> messages = new ListView<String>(messageList);

        // userTf.setStyle("-fx-base: #b6e7c9;");
        VBox.setVgrow(users, Priority.ALWAYS);
        VBox.setVgrow(conversations, Priority.ALWAYS);
        VBox.setVgrow(messages, Priority.ALWAYS);
        HBox.setHgrow(input, Priority.ALWAYS);
        HBox.setHgrow(userVBox, Priority.ALWAYS);
        HBox.setHgrow(chatVBox, Priority.ALWAYS);
        HBox.setHgrow(convosVBox, Priority.ALWAYS);

        send.setMinHeight(40);
        update.setMinHeight(40);
        input.setMinHeight(40);
        addConvo.setMinHeight(40);
        addConvo.setMaxWidth(Double.MAX_VALUE);
        userTf.setMaxWidth(Double.MAX_VALUE);
        userTf.setMinHeight(30);
        chatTf.setMaxWidth(Double.MAX_VALUE);
        chatTf.setMinHeight(30);
        convosTf.setMaxWidth(Double.MAX_VALUE);
        convosTf.setMinHeight(30);
        userVBox.setMaxWidth(150);
        chatVBox.setMaxWidth(Double.MAX_VALUE);
        convosVBox.setMaxWidth(150);

        hboxInput.getChildren().addAll(input, send, update);
        userVBox.getChildren().addAll(userTf, users);
        chatVBox.getChildren().addAll(chatTf, messages, hboxInput);
        convosVBox.getChildren().addAll(convosTf, conversations, addConvo);
        hboxClient.getChildren().addAll(userVBox, chatVBox, convosVBox);
        container.setCenter(hboxClient);
        // container.setTop(menuBar);

        mainScene = new Scene(container, WIDTH, HEIGHT);
        thestage.setScene(signInScene);
        thestage.show();
    }

    public void ButtonClicked(ActionEvent e)
    {
        thestage.setScene(mainScene);
    }
}
