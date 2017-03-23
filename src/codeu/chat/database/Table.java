package codeu.chat.database;

import java.sql.*;

public abstract class Table<S extends Schema> {

  private final S schema;
  private final Database database;
  private final String name;

  public Table(S schema, Database database, String name) throws SQLException {
    this.schema = schema;
    this.database = database;
    this.name = name;

    // Create the table.
    schema.createTable(name, database);
  }

  /**
   * Destroy the table.

   * @throws SQLException If an SQL error occurs.
   */
  public void destroy() throws SQLException {
    schema.dropTable(name, database);
  }

  /**
   * Get the schema associated with the table.
   *
   * @return The schema.
   */
  public S getSchema() {
    return schema;
  }

}
