package codeu.chat.client.simplegui2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;
import codeu.chat.client.ClientContext;

public class MainGui {

    private final static Logger.Log LOG = Logger.newLog(MainGui.class);

    private Stage windowPrime;

    private final ClientContext clientContext;


    //Constructor Method
    public MainGui(Controller controller, View view) {

        clientContext=new ClientContext(controller, view);
    }

    public void start() {
          try{
              Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("./codeu/chat/client/simplegui2/sample.fxml"));
              initialize();


          } catch (Exception ex){
              System.out.println("ERROR: Exception in ChatSimpleGui.run. Check log for details.");
              LOG.error(ex, "Exception in ChatSimpleGui.run");
              System.exit(1);
          }

    }

    private void initialize(){

        //create ChatMenu object and display stage
        ChatMenu mainFrame= new ChatMenu(clientContext);
        mainFrame.display();
    }
}

