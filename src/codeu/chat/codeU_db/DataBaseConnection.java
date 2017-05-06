package codeu.chat.codeU_db;

import java.sql.*;

public class DataBaseConnection{
    private static Connection c = null;

    public static Connection open(){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return c;
    }
    public static void createTables(){
        Connection c = null;
        Statement stmt = null;

        // Fix the issue with DataBaseConnection.open();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:./bin/codeu/chat/codeU_db/ChatDatabase.db");
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE USERS " +
                    "(ID            VARCHAR(36) PRIMARY KEY NOT NULL," +
                    " NAME          CHAR(25)                NOT NULL, " +
                    " TimeCreated   TIMESTAMP               NOT NULL, " +
                    " PASSWORD      TEXT                    NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table <USERS> created successfully");

        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE CONVERSATIONS " +
                    "(ID            VARCHAR(36) PRIMARY KEY NOT NULL, " +
                    " NAME          CHAR(25)                NOT NULL, " +
                    " TimeCreated   TIMESTAMP               NOT NULL, " +
                    " OWNERID       VARCHAR(36)             NOT NULL, " +
                    " FOREIGN KEY(OWNERID) REFERENCES USERS(ID))";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table <CONVERSATIONS> created successfully");

        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE USER_CONVERSATION " +
                    "(ID                VARCHAR(36) PRIMARY KEY NOT NULL, " +
                    " USERID            VARCHAR(36)             NOT NULL, " +
                    " CONVERSATIONID    VARCHAR(36)             NOT NULL, " +
                    " FOREIGN KEY(USERID) REFERENCES USERS(ID), " +
                    " FOREIGN KEY(CONVERSATIONID) REFERENCES CONVERSATIONS(ID))";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table <USER_CONVERSATION> created successfully");

        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE MESSAGES " +
                    "(ID                VARCHAR(36) PRIMARY KEY NOT NULL, " +
                    " USERID            VARCHAR(36)             NOT NULL, " +
                    " CONVERSATIONID    VARCHAR(36)             NOT NULL, " +
                    " TimeCreated       TIMESTAMP       NOT NULL, " +
                    " MESSAGE           TEXT            NOT NULL, " +
                    " FOREIGN KEY(USERID) REFERENCES USERS(ID), " +
                    " FOREIGN KEY(CONVERSATIONID) REFERENCES CONVERSATIONS(ID))";
            stmt.executeUpdate(sql);
            stmt.close();

            c.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table <MESSAGES> created successfully");

        System.out.println("Finished Creating Database Successfully!!!");
    }
    public static void dropTables(){
        Statement stmt = null;
        //drop tables before creating them
        try {
            stmt = c.createStatement();
            String sql = "DROP TABLE USERS";
            stmt.executeUpdate(sql);
            stmt.close();

            stmt = c.createStatement();
            sql = "DROP TABLE CONVERSATIONS";
            stmt.executeUpdate(sql);
            stmt.close();

            stmt = c.createStatement();
            sql = "DROP TABLE USER_CONVERSATION";
            stmt.executeUpdate(sql);
            stmt.close();

            stmt = c.createStatement();
            sql = "DROP TABLE MESSAGES";
            stmt.executeUpdate(sql);
            stmt.close();

        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public static Connection getConnection(){
        return c;
    }
    public static void close(){
        try {
            c.close();
        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}