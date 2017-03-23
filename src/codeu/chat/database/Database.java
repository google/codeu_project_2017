package codeu.chat.database;

import java.sql.*;

import codeu.chat.util.Logger;

public final class Database {

  private final static Logger.Log LOG = Logger.newLog(Database.class);

  private String m_dbPath;
  private Connection m_connection;

  public Database(String dbPath) {
      m_dbPath = dbPath;
  }

  /**
   * Attempts to validate the connection.
   * If the connection is closed or does not exist, the function will try
   * to reconnect.
   *
   * @return Whether the connection was succesfully validated.
   */
  private boolean validateConnection() {
    try {
      // If the connection is null or is closed, create a new one.
      if (m_connection == null || !m_connection.isValid(0)) {
        Class.forName("org.sqlite.JDBC");
        m_connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", m_dbPath));
      }

      // Any other state implies the connection is valid.
      return true;
    } catch (Exception ex) {
      LOG.error("Database failed to connect: %s", ex.getMessage());
      return false;
    }
  }

  /**
   * Get the current database connection.
   * The function will attempt to validate the connection. If the
   * connection cannot be validated, then the function returns null.
   *
   * @return The current database connection.
   */
  public Connection getConnection() {
    if (validateConnection()) {
      return m_connection;
    }
    return null;
  }

}
