package codeu.chat.util;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class queryDB {
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:/Users/jxu8/Desktop/codeu_project_2017/chat.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void selectUser(){
        String sql = "SELECT user_id, username FROM users";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                System.out.println(rs.getString("user_id") + "\t" + rs.getString("username"));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectChatRoom(){
        String sql = "SELECT room_id, roomname FROM chatRoom";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                System.out.println(rs.getString("room_id") + "\t" + rs.getString("roomname"));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectMessage(){
        String sql = "SELECT message_id, user_id, room_id, content FROM messages";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                System.out.println(rs.getString("message_id")
                        + "\t" + rs.getString("user_id")
                        + "\t" + rs.getString("room_id")
                        + "\t" + rs.getString("content"));
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    public static void main(String[] args) {
        queryDB query = new queryDB();
        query.selectMessage();
    }
}
