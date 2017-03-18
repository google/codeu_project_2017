package databaseTester;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connector {

  Statement myStmt;

  Connector() {
    try {
      
      //the parameters differ based on the machine you are using and the user account you have
      //DriverManager.getConnection(url, user, password)
      //URL:"jdbc:mysql://your IP address:3306/codeU_database?useSSL=false" 
      //user: guven user account that is used to access to the database
      //password: password that assoicated with the account
      Connection myConn = DriverManager.getConnection(
          "jdbc:mysql://IP of remote machine:3306/codeU_database?useSSL=false", "username", "password");
      
      myStmt = myConn.createStatement();
    } catch (SQLException ex) {
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
  }

  public static void main(String[] args) {

    Connector test = new Connector();
    /*
     * // TEST CASE 1: insert duplicate fail boolean success1 = test.addAccount("user", "hello"); if
     * (success1) System.out.println("user1 has been created, and insertion is complete"); else
     * System.out.println("insertion fails");
     * 
     * // TEST CASE 2: insert success boolean success2 = test.addAccount("user3", "hello"); if
     * (success2) System.out.println("user3 has been created, and insertion is complete"); else
     * System.out.println("insertion fails");
     * 
     * // TEST CASE 3: Delete success boolean success3 = test.deleteAccount("user3"); if (success3)
     * System.out.println("user3 has been deleted"); else System.out.println("deletion fails");
     * 
     * // TEST CASE 4: Delete fail boolean success4 = test.deleteAccount("userUnknown"); if
     * (success4) System.out.println("userUnknown has been deleted"); else System.out.println(
     * "deletion fails");
     * 
     * // TEST CASE 5: update Password boolean success5 = test.updatePassword("user2",
     * "new password"); if (success5) System.out.println("password is updated"); else
     * System.out.println("update fails");
     * 
     * // TEST CASE 8: insert success boolean success8 = test.addAccount("userTest", "hello"); if
     * (success8) System.out.println("userTest has been created, and insertion is complete"); else
     * System.out.println("insertion fails");
     * 
     * // TEST CASE 6: verify true boolean success6 = test.updatePassword("userTest", "he"); if
     * (success6) System.out.println("the password has been updated"); else System.out.println(
     * "update fails");
     * 
     * // TEST CASE 7: verify false; boolean success7 = test.updatePassword("usertTest", "he"); if
     * (success7) System.out.println("the account has been verified"); else System.out.println(
     * "invalid account info");
     */

    // TEST CASE 9: verify false;
    boolean success9 = test.verifyAccount("usertTest", "hello");
    if (success9)
      System.out.println("the account has been verified");
    else
      System.out.println("invalid account info");
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
    String sqlAdd = "insert into AccountInfo" + "(username, password)" + "values(" + "'" + username
        + "'" + "," + "'" + password + "'" + ")";
    String sqlCount = "select count(*) from AccountInfo";
    String sqlSelect = "select username from AccountInfo where username = " + "'" + username + "'";

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
    String sqlSelect = "select username from AccountInfo where username = " + "'" + username + "'";
    String sqlPassword =
        "select password from AccountInfo where username = " + "'" + username + "'";

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
   * @param usernmae
   * @return false if the deletion fails; true if succeeds
   */
  public boolean deleteAccount(String username) {
    // construct string to delete the account
    String sqlDelete =
        "DELETE  FROM codeU_database.AccountInfo WHERE username = '" + username + "'";
    String sqlSelect =
        "SELECT username FROM codeU_database.AccountInfo WHERE username = '" + username + "'";
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
    String sqlUpdate = "UPDATE codeU_database.AccountInfo SET password = '" + newPassword
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
