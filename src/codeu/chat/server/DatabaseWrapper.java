
package codeu.chat.server;

import codeu.chat.util.Logger;
import codeu.chat.common.User;
import codeu.chat.common.Message;
import codeu.chat.common.Conversation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseWrapper {
    private Connection dbConn = null;
    private String dbUrl = "jdbc:sqlite:db/main.db";

    private final static Logger.Log LOG = Logger.newLog(DatabaseWrapper.class);

    public  DatabaseWrapper() {

        try{
            Class.forName("org.sqlite.JDBC");
            dbConn = DriverManager.getConnection(dbUrl);
            LOG.info("Conndeted to Database: %s", dbUrl);
        } catch (SQLException e){
            LOG.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage());
        }
    }

    public void addUser(User user){
        try{
            Statement stmnt = dbConn.createStatement();
            String sql = "INSERT INTO Users(id,username) VALUES("
                    + user.id.id()
                    + ", \""
                    + user.name
                    + "\")";
            stmnt.execute(sql);
            LOG.info(stmnt.toString());
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
    }

    public void addMessage(Message message){

    }

    public void addConversation(Conversation conversation){

    }

}
