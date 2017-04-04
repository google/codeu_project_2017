package codeu.chat.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * referenced from sqlitetutorial.net and utilizes Java JDBC Sqlite API
 */

public class buildDB {


    private static void createNewDatabase(String user, String chatRoom, String messages) {

        String url = "jdbc:sqlite:/Users/jxu8/Desktop/codeu_project_2017/chat.db";

        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database chat.db has been created.");
                stmt.execute(user);
                stmt.execute(chatRoom);
                stmt.execute(messages);
                System.out.println("user, chatRoom, and messages tables have been added");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String[] args) {

        // create tables for users, chat rooms, and messages
        String sqlUser = "CREATE TABLE IF NOT EXISTS users (\n"
                + " user_id text PRIMARY KEY,\n"
                + " username text NOT NULL UNIQUE\n"
                + ") WITHOUT ROWID;";

        String sqlChatRoom = "CREATE TABLE IF NOT EXISTS chatRoom (\n"
                + " room_id text PRIMARY KEY,\n"
                + " roomname text NOT NULL\n"
                + ") WITHOUT ROWID;";


        // message table references user_id and room_id from users table and chatroom table respectively
        String sqlMessageTable = "CREATE TABLE IF NOT EXISTS messages (\n"
                + " message_id text,\n"
                + " user_id text,\n"
                + " room_id text,\n"
                + " content text NOT NULL,\n"
                + " PRIMARY KEY (message_id, user_id, room_id),\n"
                + " FOREIGN KEY (user_id) REFERENCES users (user_id),\n"
                + " FOREIGN KEY (room_id) REFERENCES chatRoom (room_id)\n"
                + ") WITHOUT ROWID;";

        createNewDatabase(sqlUser, sqlChatRoom, sqlMessageTable);
    }
}
