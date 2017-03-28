package DBConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
  public String[] getAllUsers() {

    String sqlSelectCount = "select count(*) from "+tableName;
    String sqlSelectUsername = "select username from " + tableName;
    String[] userNames = null;

    try(PreparedStatement selectCount = myConn.prepareStatement(sqlSelectCount)) {
      try (ResultSet getSize = selectCount.executeQuery()) {
        getSize.next();
        int size = getSize.getInt(1);
        userNames = new String[size];
        if (size == 0) {
          System.out.println("the table is empty");
          return userNames;
        }
      }
    } catch (SQLException e1){e1.printStackTrace(); return userNames;}

    //if the size is not 0, and there are users indeed
    try(PreparedStatement getUsers = myConn.prepareStatement(sqlSelectUsername)){
      try(ResultSet users= getUsers.executeQuery()){
        int i = 0;
        while (users.next()) {
          userNames[i]= users.getString("username");
          i++;
        }
        return userNames;
      }
    } catch (SQLException e) {e.printStackTrace();return userNames;}
  }

  /**
   * AddAccount is to add the new account to the database.
   * 
   * @param username
   * @param password
   * @return true if the insertion is successful and complete; false, if the insertion fails
   */
  public boolean addAccount(String username, String password) {

    String sqlSelectUsername = "select username from "+tableName+" where username = ?";
    String sqlInsertAccount = "insert into "+tableName + "(username, password)" + "values(?,?)";
    String sqlSelectCount = "select count(*) from "+tableName;

    int size = 0;
    try( PreparedStatement selectCount = myConn.prepareStatement(sqlSelectCount)){
      try(ResultSet getSize = selectCount.executeQuery()) {
        if (getSize.next()) {
          size = getSize.getInt(1);
          getSize.beforeFirst();
        }
      }
    }catch (SQLException e1){e1.printStackTrace();return false;}

    if (size == 0){
      try (PreparedStatement insertAccount = myConn.prepareStatement(sqlInsertAccount)) {
        insertAccount.setString(1, username);
        insertAccount.setString(2, password);
        insertAccount.executeUpdate();
        return true;
      } catch (SQLException e2){e2.printStackTrace();return false;}
    }

    //if there are more than one user, return false if there exists one target already
    try(PreparedStatement selectUsername2 = myConn.prepareStatement(sqlSelectUsername)){
      selectUsername2.setString(1,username);
      try(ResultSet getUser = selectUsername2.executeQuery()){
        if (getUser.next()) {
          // move the cursor all the way back to the starting point
          getUser.beforeFirst();
          System.err.println("the account exists");
          return false;
        }
      }
    } catch (SQLException e) {e.printStackTrace();return false;}

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
    String sqlSelectUsername = "select username from "+tableName+" where username = ?";
    boolean accountExits = false;

    try(PreparedStatement selectUsername = myConn.prepareStatement(sqlSelectUsername)) {
      selectUsername.setString(1, username);
      try (ResultSet resultUsername = selectUsername.executeQuery()) {
        // check the existence
        if (resultUsername.next()) {
          resultUsername.beforeFirst();
          accountExits = true;
        }
      }
    } catch (SQLException e1){e1.printStackTrace(); return false;}

    if(accountExits){
      try (PreparedStatement selectPassword = myConn.prepareStatement(sqlSelectPassword)){
        // the account exists, check password
        selectPassword.setString(1, username);
        try (ResultSet resultPassword = selectPassword.executeQuery()) {
          if (resultPassword.next()) {
            // get the stored password from database
            String passwordInDB = resultPassword.getString(1);
            resultPassword.beforeFirst();
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
      catch (SQLException e2){e2.printStackTrace(); return false;}
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
    String sqlSelectUsername = "select username from "+tableName+" where username = ?";

    try(PreparedStatement selectUsername = myConn.prepareStatement(sqlSelectUsername)) {
      selectUsername.setString(1, username);
      try (ResultSet getUsername = selectUsername.executeQuery()) {
        if (getUsername.next()) {
          getUsername.beforeFirst();
          try (PreparedStatement deleteAccount = myConn.prepareStatement(sqlDeleteAccount)) {
            deleteAccount.setString(1, username);
            deleteAccount.executeUpdate();
            return true;
          }
        }
        //if the account does not exist
        return false;
      }
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