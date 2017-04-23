package codeu.chat.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class insertDB {

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

    public void insertUser(String users_id, String username) {
        String sql = "INSERT INTO users(user_id, username) VALUES(?,?)";
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, users_id);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            System.out.println("user insertion successful");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertChatRoom(String room_id, String roomname) {
        String sql = "INSERT INTO chatRoom(room_id, roomname) VALUES(?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room_id);
            pstmt.setString(2, roomname);
            pstmt.executeUpdate();
            System.out.println("chat room insertion successful");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertMessage(String message_id, String user_id, String room_id, String content) {
        String sql = "INSERT INTO messages(message_id, user_id, room_id, content) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, message_id);
            pstmt.setString(2, user_id);
            pstmt.setString(3, room_id);
            pstmt.setString(4, content);
            pstmt.executeUpdate();
            System.out.println("message insertion successful");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /*public static void main(String[] args) {
        insertDB app = new insertDB();

        // dummy data for testing insertion
        app.insertUser("1", "Jonny");
        app.insertChatRoom("5", "friends");
        app.insertMessage("10", "1", "5", "hey there buddy");
    }*/

}
