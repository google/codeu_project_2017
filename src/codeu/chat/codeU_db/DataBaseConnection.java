package codeu.chat.codeU_db;

import java.sql.*;

public class DataBaseConnection{
    private static Connection c = null;

    public static void open(){
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    public static void close() throws Exception{
        c.close();
    }
    public static Statement createStatment() throws Exception{
        return c.createStatement();
    }
}