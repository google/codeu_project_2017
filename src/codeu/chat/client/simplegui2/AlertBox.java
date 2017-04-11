package codeu.chat.client.simplegui2;

import codeu.chat.client.ClientContext;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by noramyer on 4/6/17.
 * When called, creates a pop-up window displaying message that must be exited before user can continue to use program
 */
public final class AlertBox {

    private Stage window;
    private Label label1;
    private Button closeButton;

    public AlertBox(){

        initialize();
    }
    public void display(String title, String message) {

        //Initialize title
        window.setTitle(title);

        //set Label message
        label1.setText(message);

        //shows window and waits to be closed before returning to main
        window.showAndWait();
    }
    private void initialize(){

        //creates window and sizing
        window = new Stage();
        window.setMinWidth(500);
        window.setMaxHeight(200);

        //Initialize label
        label1 = new Label();

        //inhibits user functions while window is open
        window.initModality(Modality.APPLICATION_MODAL);


        //create Layout
        VBox layout = new VBox();
        layout.getChildren().addAll(label1, closeButton);

        //set Window
        Scene mainScene = new Scene(layout);
        window.setScene(mainScene);


    }

}
