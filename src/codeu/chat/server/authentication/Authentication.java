package codeu.chat.server.authentication;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codeu.chat.database.Database;
import codeu.chat.database.DBObject;
import codeu.chat.server.database.UserTable;
import codeu.chat.server.database.UserSchema;

import codeu.chat.authentication.AuthenticationCode;

import codeu.chat.util.Logger;

/**
 * Authentication manager.
 */
public final class Authentication {

  private static final Logger.Log LOG = Logger.newLog(Authentication.class);

  private final Database database;

  private UserTable userTable;

  /**
   * Creates an authentication manager.
   *
   * @param database The server database.
   */
  public Authentication(Database database) {
    this.database = database;

    try {
      userTable = new UserTable(database);
    } catch (SQLException ex) {
      LOG.error(ex, "Failed to initialize user table.");
      System.exit(1);
    }
  }

  /**
   * Register a user.
   *
   * @param username The username of the new user.
   * @param password The password of the new user.
   *
   * @return An error code signifying the result of registration.
   */
  public int register(String username, String password) {
    // Verify that the input is valid.
    username = username.trim();
    if (username.length() == 0 || username.length() > 255) {
      return AuthenticationCode.REGISTER_INVALID_INPUT;
    }
    if (password.length() == 0) {
      return AuthenticationCode.REGISTER_INVALID_INPUT;
    }

    try {
      // Verify that a user with the username doesn't already exist.
      boolean userExists = userTable.findQuery("LOWER(username) = LOWER(?)", username).size() > 0;
      if (userExists) {
        return AuthenticationCode.REGISTER_USER_EXISTS;
      }

      // Salt and hash the password.
	    String salt = BCrypt.gensalt();
	    String hashed = BCrypt.hashpw(password, salt);

      // Store the data.
      Map<String, String> fields = new HashMap<String, String>();
      fields.put("username", username);
      fields.put("password", hashed);
      fields.put("salt", salt);
      userTable.create(fields);

      // Registration successful.
      return AuthenticationCode.SUCCESS;
    } catch (SQLException ex) {
      LOG.error(ex, "Failed to register user.");
      return AuthenticationCode.DB_ERROR;
    }
  }

  /**
   * Attempt to log a user in.
   *
   * @param username The username of the user.
   * @param password The password of the user.
   *
   * @return An error code signifying the result of logging in.
   */
  public int login(String username, String password) {
    username = username.trim();

    try {
      // Check if the user exists.
      List<DBObject<UserSchema>> foundUsers = userTable.findQuery("LOWER(username) = LOWER(?)", username);
      if (foundUsers.size() == 0) {
        // No users found. More specific information is not given for the sake of security.
        return AuthenticationCode.LOGIN_FAILED;
      }
      DBObject<UserSchema> user = foundUsers.get(0);

      // Verify that the password matches.
      String salt = user.get("salt");
      String hashed = user.get("password");
      String hashedInput = BCrypt.hashpw(password, salt);
      if (!hashed.equals(hashedInput)) {
        // Incorrect password. More specific information is not given for the sake of security.
        return AuthenticationCode.LOGIN_FAILED;
      }

      // Registration successful.
      return AuthenticationCode.SUCCESS;
    } catch (SQLException ex) {
      LOG.error(ex, "Failed to register user.");
      return AuthenticationCode.DB_ERROR;
    }
  }

}
