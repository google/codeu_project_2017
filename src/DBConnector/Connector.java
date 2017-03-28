package DBConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Please read here:
 * ****to run this code, you will need to install jdbc connector that connects the java program to the databse.
 * jdbc is the jar file, and you will need to import it to the referenced library.
 * 
 * Description:
 * this is the database connector you will need to use to communicate with the databased hosted on a remote machine.
 * Pass in the IP of the remote machine where the database is hosted, also your authorized username and password to the database.
 *  we do not nee to run the mySQL server.
 */

public class Connector {

   //private static Statement myStmt;
   private Connection myConn;
   private static String tableName = "UserAccount";
   private static final String dbName = "CodeU_2017DB";
   private static final String hostname = "ec2-176-34-225-252.eu-west-1.compute.amazonaws.com";
   private static final String DBusername = "group34";
   private static final String DBpassword = "codeu2017";

   //the constructor can only create once per run.
   private
   PreparedStatement delete, getUsers, add, update, verify_selectPassword, selectUsername, dropAll, selectCount;

  public Connector() {
    try {
      myConn = DriverManager.getConnection(
          "jdbc:mysql://" + Connector.hostname + ":3306/" + Connector.dbName + "?useSSL=false",
          Connector.DBusername, Connector.DBpassword);

      /*for deleteAccount()*/
      delete = myConn.prepareStatement("DELETE  FROM "+tableName+" WHERE username = ?");

      /*for updateAcount()*/
      update = myConn.prepareStatement("UPDATE "+tableName+" SET password = ? WHERE username = ?");

      /*for verifyAccount():*/
      verify_selectPassword = myConn.prepareStatement("select password from "+tableName+" where username = ?");
      //shared by both verify and add
      selectUsername = myConn.prepareStatement("select username from "+tableName+" where username = ?");

      /*for addUser():*/
      add = myConn.prepareStatement("insert into "+tableName + "(username, password)" + "values(?,?)");
      selectCount = myConn.prepareStatement("select count(*) from "+tableName);
      /*selectUsername*/

      /*for DropAllUsers():*/
      dropAll = myConn.prepareStatement(" truncate table "+ tableName);

      /*for printAllUsers():*/
      getUsers = myConn.prepareStatement("select username from " + tableName);

    } catch (SQLException ex) {
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
  }

  /**
   * print all the current usernames
   */
  public String[] getAllUsers() {
    ResultSet result = null;
    ResultSet resultSize = null;
    try {
      resultSize = selectCount.executeQuery();
      resultSize.next();
      int size = resultSize.getInt(1);
      String[] userNames = new String[size];

      if (! result.first()) {
        System.out.println("the table is empty");
        resultSize.close();
        return null;
      }
      else {
        result= getUsers.executeQuery();
        int i = 0;
        while (result.next()) {
          userNames[i]= result.getString("username");
          i++;
        }
        result.close();
        resultSize.close();
        return userNames;
      }
    }
    catch (SQLException e) {
      try {result.close();resultSize.close();} catch (SQLException e1) {e1.printStackTrace();}
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

    ResultSet resultSize = null, resultUser= null;
    try {
      // acquire the table size from the database, and initialize the table size
      resultSize = selectCount.executeQuery();
      int size = 0;
      // move the cursor one row forward, if there is a row, return true; and reassign value to size
      if (resultSize.next()) {
        size = resultSize.getInt(1);
        // move the cursor all the way back to the starting point
        resultSize.beforeFirst();
      }
      // if the table is empty, we directly insert the new account
      if (size == 0) {
        add.setString(1,username);
        add.setString(2,password);
        add.executeUpdate();
        resultSize.close();
        return true;
      }
      /*
       * if there exists at least one element, check the existence; we first get the array of row
       * that has required username
       */
      selectUsername.setString(1,username);
      resultUser = selectUsername.executeQuery();
      // check if there is any row that have the required username
      if (resultUser.next()) {
        // move the cursor all the way back to the starting point
        resultUser.beforeFirst();
        resultSize.close();
        resultUser.close();
        return false;
      }

      // the newly created username does not exist in the database, then add it to the database
      add.setString(1,username);
      add.setString(2,password);
      add.executeUpdate();
      resultSize.close();
      resultUser.close();
      return true;
    }
    catch (SQLException e) {
      System.err.println(e.getMessage());
      try {
        resultSize.close();
        resultUser.close();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
      return false;
    }
  }

  /**
   * clean all the data inside the database
   *
   * @return true if the data has been cleaned
   */

  boolean dropAllAccounts() {
    try{
        dropAll.executeUpdate();
        System.out.println("the table has been cleared");
    }
    catch(SQLException e){
      System.out.println(e.getMessage());
      return false;
    }
    return true;
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
    ResultSet resultUsername = null, resultPassword = null;
    try {
      // get the available username match from the database
      selectUsername.setString(1,username);
      resultUsername = selectUsername.executeQuery();

      // check the existence
      if (resultUsername.next()) {
        resultUsername.beforeFirst();

        // the account exists, check password
        verify_selectPassword.setString(1,username);
        resultPassword = verify_selectPassword.executeQuery();
        if (resultPassword.next()) {

          // get the stored password from database
          String passwordInDB = resultPassword.getString(1);
          resultPassword.beforeFirst();

          // password does not match
          if (!passwordInDB.equals(password)) {
            resultUsername.close();
            resultPassword.close();
            return false;
          }
          // the account exits and log in successfully
          resultPassword.close();
          resultUsername.close();
          return true;
        }
      }

      // the account does not exit
      resultUsername.close();
      return false;

    }
    catch (SQLException e) {
      System.err.println(e.getMessage());
      try {
        resultPassword.close();
        resultUsername.close();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
      return false;
    }
  }

  /**
   * delete the existing account
   * 
   * @param username
   * @return false if the deletion fails; true if succeeds
   */
  public boolean deleteAccount(String username) {
    ResultSet result = null;
    try {
      selectUsername.setString(1,username);
      result = selectUsername.executeQuery();
      if (result.next()) {
        result.beforeFirst();
        delete.setString(1,username);
        delete.executeUpdate();
        result.close();
        return true;
      }
      result.close();
      return false;
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      try {
        result.close();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
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

    try {
      update.setString(1,newPassword);
      update.setString(2,username);
      update.executeUpdate();
      System.out.println("the account has been updated");
    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
      return false;
    }
    return true;
  }

  /**
   * when all has been done with database, call close to end the connection.
   * can restart by creating a new instance of connector
   */
  public void closeConnection(){
    try{
      delete.close();
      getUsers.close();
      add.close();
      update.close();
      verify_selectPassword.close();
      selectUsername.close();
      dropAll.close();
      selectCount.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try{myConn.close();}catch (SQLException e) {e.printStackTrace();}
  }
}
