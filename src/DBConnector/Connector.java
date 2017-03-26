package DBConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

 /**
  * Please read here:
  * to run this code, you will need to install jdbc connector that connects the java program to
  * the database. JDBC is the jar file, and you will need to import it to the referenced library.
  * In order to check if the data has been written in the database, you can either use terminal
  * or mysqlWorkbench to view the table. But, you will need to connect to the database first by
  *  using the provided hostname, username, and password (see field variables);
  */

 /**
  * Description:
  * this is the database connector you will need to use to communicate with the databased hosted on
  * a remote machine. Pass in the IP of the remote machine where the database is hosted, also your
  * authorized username and password to the database. we do not need to run mySQL server.
  */

public class Connector {

  Statement myStmt;
  String tableName = "UserAccount";
  String dbName = "CodeU_2017DB";
  String hostname = "ec2-176-34-225-252.eu-west-1.compute.amazonaws.com";
  String DBusername = "group34";
  String DBpassword = "codeu2017";

  public Connector() {
    try {
      Connection myConn = DriverManager.getConnection(
          "jdbc:mysql://"+this.hostname+":3306/"+this.dbName+"?useSSL=false",
           this.DBusername, this.DBpassword);
      myStmt = myConn.createStatement();
      
    } catch (SQLException ex) {
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
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

    /* string constructed for database */
    String sqlAdd = "insert into "+tableName + "(username, password)" + "values(" + "'" + username
        + "'" + "," + "'" + password + "'" + ")";
    String sqlCount = "select count(*) from "+tableName;
    String sqlSelect =
        "select username from "+tableName+" where username = " + "'" + username + "'";

    try {
      // acquire the table size from the database, and initialize the table size
      ResultSet resultSize = myStmt.executeQuery(sqlCount);
      int size = 0;

      // move the cursor one row forward, if there is a row, return true; and reassign value to size
      if (resultSize.next()) {
        size = resultSize.getInt(1);
        // move the cursor all the way back to the starting point
        resultSize.beforeFirst();
      }

      // if the table is empty, we directly insert the new account
      if (size == 0) {
        myStmt.executeUpdate(sqlAdd);
        return true;
      }

      /*
       * if there exists at least one element, check the existence; we first get the array of row
       * that has required username
       */
      ResultSet result = myStmt.executeQuery(sqlSelect);

      // check if there is any row that have the required username
      if (result.next()) {
        // move the cursor all the way back to the starting point
        result.beforeFirst();
        return false;
      }

      // the newly created username does not exist in the database, then add it to the database
      myStmt.executeUpdate(sqlAdd);
      return true;

    } catch (SQLException e) {
      System.err.println(e.getMessage());
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

    // String constructed to check if the account exits and password matches
    String sqlSelect =
        "select username from "+tableName+" where username = " + "'" + username + "'";
    String sqlPassword =
        "select password from "+tableName+" where username = " + "'" + username + "'";

    try {

      // get the available username match from the database
      ResultSet result = myStmt.executeQuery(sqlSelect);

      // check the existence
      if (result.next()) {
        result.beforeFirst();

        // the account exists, check password
        ResultSet code = myStmt.executeQuery(sqlPassword);
        if (code.next()) {

          // get the stored password from database
          String passwordInDB = code.getString(1);
          code.beforeFirst();

          // password does not match
          if (!passwordInDB.equals(password)) {
            return false;
          }
          // the account exits and log in successfully
          return true;
        }
      }

      // the account does not exit
      return false;

    } catch (SQLException e) {
      System.err.println(e.getMessage());
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
    // construct string to delete the account
    String sqlDelete =
        "DELETE  FROM "+tableName+" WHERE username = '" + username + "'";
    String sqlSelect =
        "SELECT username FROM "+tableName+" WHERE username = '" + username + "'";
    try {
      ResultSet result = myStmt.executeQuery(sqlSelect);
      if (result.next()) {
        result.beforeFirst();
        myStmt.executeUpdate(sqlDelete);

        return true;
      }
      return false;
    } catch (SQLException e) {
      System.err.println(e.getMessage());
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
    // construct string that updates the password in the database
    String sqlUpdate = "UPDATE "+tableName+" SET password = '" + newPassword
        + "' WHERE username = '" + username + "'";
    try {
      myStmt.executeUpdate(sqlUpdate);
    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
      return false;
    }
    return true;
  }
}
