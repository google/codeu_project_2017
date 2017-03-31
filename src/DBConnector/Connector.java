package DBConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import sun.awt.image.ImageWatched.Link;

/**
 * JDBC connector is needed to connects java program to the database.
 * Description:
 * this is the database connector you will need to use to communicate with
 * the databased hosted on a remote machine.
 */

public class Connector {

  private Connection myConn;
  private static String tableName = "UserAccount";
  private static final String dbName = "CodeU_2017DB";
  private static final String hostname = "ec2-176-34-225-252.eu-west-1.compute.amazonaws.com";
  private static final String DBusername = "group34";
  private static final String DBpassword = "codeu2017";

  public Connector() {
    try {
      myConn = DriverManager.getConnection(
          "jdbc:mysql://" + Connector.hostname + ":3306/" + Connector.dbName + "?useSSL=false",
          Connector.DBusername, Connector.DBpassword);
    } catch (SQLException ex) {
      // handle any errors
      System.err.println("SQLException: " + ex.getMessage());
      System.err.println("SQLState: " + ex.getSQLState());
      System.err.println("VendorError: " + ex.getErrorCode());
    }
  }

  /**
   * print all the current useNnames
   */
  public List<String> getAllUsers() {

    String sqlSelectUsername = "select username from " + tableName;
    LinkedList<String> userNames = new LinkedList<>();

    try(PreparedStatement getUsers = myConn.prepareStatement(sqlSelectUsername)){
      try(ResultSet users= getUsers.executeQuery()){
        while (users.next()) {userNames.add(users.getString("username"));}
        return userNames;
      }
    }
    catch (SQLException e) {
      System.out.println("the error occurred from database");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * AddAccount is to add the new account to the database.
   * 
   * @param username
   * @param password
   * @return true if the insertion is successful and complete; false, if the insertion fails
   */
  public boolean addAccount(String username, String password) {

    String sqlInsertAccount = "insert into "+tableName + "(username, password)" + "values(?,?)";

    try(PreparedStatement insertAccount = myConn.prepareStatement(sqlInsertAccount)) {
      insertAccount.setString(1, username);
      insertAccount.setString(2, password);
      insertAccount.executeUpdate();
      return true;
    } catch (SQLException e3) {e3.printStackTrace();return false;}
  }

  /**
   * clean all the data inside the database
   *
   * @return true if the data has been cleaned
   */
  public boolean dropAllAccounts() {

    String sqlDrop = "truncate table "+ tableName;

    try(PreparedStatement dropAll = myConn.prepareStatement(sqlDrop)){
        dropAll.executeUpdate();
        System.out.println("the table has been cleared");
        return true;
    }
    catch(SQLException e){
      System.err.println(e.getMessage());
      System.err.println("the table is not able to be cleared");
      return false;
    }
  }

  /**
   * verify if the account username and password input by users match what has been recorded in
   * database
   * 
   * @param username
   * @param password
   * @return true if the account is verified, else, false, if the account is not valid
   */
  public boolean verifyAccount(String username, String password) {

    String sqlSelectPassword = "select password from "+tableName+" where username = ?";

    try (PreparedStatement selectPassword = myConn.prepareStatement(sqlSelectPassword)){
      // the account exists, check password
      selectPassword.setString(1, username);
      try (ResultSet resultPassword = selectPassword.executeQuery()) {
        if (resultPassword.next()){
          // get the stored password from database
          String passwordInDB = resultPassword.getString(1);
          // password does not match
          if (!passwordInDB.equals(password)) {
            System.err.println("the password does not match");
            return false;
          }
          System.out.println("the account exists");
          return true;
        }
      }
    }
    catch (SQLException e2){
      e2.printStackTrace();
      return false;
    }
    System.err.println("the account does not exist");
    return false;
  }

  /**
   * delete the existing account; deletion requires the user to sign in first
   * 
   * @param username
   * @return false if the deletion fails; true if succeeds
   */
  public boolean deleteAccount(String username) {

    String sqlDeleteAccount = "DELETE  FROM "+tableName+" WHERE username = ?";

    try (PreparedStatement deleteAccount = myConn.prepareStatement(sqlDeleteAccount)) {
      deleteAccount.setString(1, username);
      if(deleteAccount.executeUpdate() == 1)return true;
      else return false;
    }
    catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * update the existing account's password we assume that the change of password can only happen
   * after the user has logged in
   * 
   * @param username
   * @param newPassword
   * @return true if the update succeeds, else, false;
   */

  public boolean updatePassword(String username, String newPassword) {
    String sqlUpdate = "UPDATE "+tableName+" SET password = ? WHERE username = ?";
    try (PreparedStatement update = myConn.prepareStatement(sqlUpdate)){
      update.setString(1,newPassword);
      update.setString(2,username);
      update.executeUpdate();
      System.out.println("the account has been updated");
    }
    catch (SQLException ex) {
      System.err.println(ex.getMessage());
      return false;
    }
    return true;
  }

  /**
   * when all has been done with database, call close to end the connection.
   * can restart by creating a new instance of connector
   */
  public void closeConnection() {
    try {
      myConn.close();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
}