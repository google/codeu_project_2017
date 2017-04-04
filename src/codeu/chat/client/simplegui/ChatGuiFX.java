package codeu.chat.client.simplegui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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


    Button signInButton;
    Label signInLabel;
    BorderPane signInPane;
    Scene signInScene, mainScene;
    Stage thestage;
    private ClientContext clientContext;

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

        Scene scene = new Scene(container, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
