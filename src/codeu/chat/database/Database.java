package codeu.chat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import codeu.chat.util.Logger;

/**
 * Handles SQL database connections.
 */
public class Database {

  private static final Logger.Log LOG = Logger.newLog(Database.class);

  private final String dbPath;
  private Connection connection;

  /**
   * Creates a database connection manager.
   *
   * @param dbPath The path to the SQLite database.
   */
  public Database(String dbPath) {
      this.dbPath = dbPath;
  }

  /**
   * Attempts to connect to the the database if needed.
   * If the connection is closed or does not exist, the function will try
   * to reconnect.
   */
  private void connectIfRequired() {
    try {
      // If the connection is null or is closed, create a new one.
      if (connection == null || !connection.isValid(0)) {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbPath));
      }

      // Return early.
      return;
    } catch (ClassNotFoundException ex) {
      LOG.error(ex, "Database failed to connect.");
    } catch (SQLException ex) {
      LOG.error(ex, "Database failed to connect.");
    }

    // Failed to connect, so end the program.
    System.exit(1);
  }

  /**
   * Get the current database connection.
   * The function will attempt to reconnect if necessary.
   *
   * @return The current database connection.
   */
  public Connection getConnection() {
    connectIfRequired();
    return connection;
  }

}
