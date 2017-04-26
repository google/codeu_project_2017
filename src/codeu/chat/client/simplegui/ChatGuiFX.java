package codeu.chat.client.simplegui;

import javafx.application.Application;
import javafx.event.ActionEvent;
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


    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 500;

    private Stage thestage;                         // Holds the scene that user is currently viewing
    private Scene signInScene, mainScene;           // Scenes to hold all the elements for each page
    private Button signInButton;
    private TextField userInput;
    private PasswordField passInput;                // Takes input for username and password
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

        this.thestage = primaryStage;                           // Initialize the main stage

        BorderPane signInPane = new BorderPane();               // Initialize panes
        FlowPane signInLabelPane = new FlowPane();
        HBox inputMasterBox = new HBox();
        inputMasterBox.setSpacing(5);
        VBox inputVBox = new VBox();
        HBox usernameHBox = new HBox();
        HBox passHBox = new HBox();

        // Set Pane alignments

        signInLabelPane.setAlignment(Pos.BOTTOM_CENTER);
        inputMasterBox.setAlignment(Pos.CENTER);
        inputVBox.setAlignment(Pos.CENTER);
        usernameHBox.setAlignment(Pos.CENTER);
        passHBox.setAlignment(Pos.CENTER);

        Label signInLabel = new Label("Sign-in screen");       // Title for sign-in page
        Label userLabel = new Label("Username:");
        Label passLabel = new Label("Password:");
        signInLabel.setFont(Font.font(20));                         // Set style
        userLabel.setFont(Font.font(15));
        passLabel.setFont(Font.font(15));

        signInButton = new Button("Sign in");                      // Initialize sign in button
        signInButton.setOnAction((event)-> buttonClicked(event));       // Initialize its event handler


        userInput = new TextField();
        passInput = new PasswordField();                        // Set up password fields
        userInput.setPromptText("Username");                    // TODO: figure out if have the prompt text is overkill
        passInput.setPromptText("Password");
        userInput.setAlignment(Pos.CENTER);
        passInput.setAlignment(Pos.CENTER);


        signInLabelPane.getChildren().add(signInLabel);         // Add labels to their respective panes

        usernameHBox.getChildren().add(userLabel);
        usernameHBox.getChildren().add(userInput);          // Set up HBoxes to hold username and password labels/inputs
        passHBox.getChildren().add(passLabel);
        passHBox.getChildren().add(passInput);

        inputVBox.getChildren().add(usernameHBox);
        inputVBox.getChildren().add(passHBox);              // Add those HBoxes to a VBox to stack them on top of each other

        inputMasterBox.getChildren().add(inputVBox);
        inputMasterBox.getChildren().add(signInButton);     // Add that VBox and the signInButton to the inputMasterBox

        signInPane.setTop(signInLabelPane);
        signInPane.setCenter(inputMasterBox);                 // Add label and input box to the pane


        signInScene = new Scene(signInPane, WINDOW_WIDTH, WINDOW_HEIGHT);



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

        mainScene = new Scene(container, WINDOW_WIDTH, WINDOW_HEIGHT);
        thestage.setScene(signInScene);
        thestage.show();
    }

    private void buttonClicked(ActionEvent e)
    {
        thestage.setScene(mainScene);           // TODO: Call a controller function here instead
    }
}
