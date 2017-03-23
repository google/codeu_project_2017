package codeu.chat.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public abstract class Schema {

  private Database m_database;

  Map<String, String> m_fields;

  public Schema(Database database) {
    m_database = database;
    m_fields = new HashMap<String, String>();
  }

  /**
   * Add a field to the schema.
   *
   * @param name The name of the field.
   * @param props The properties associated with the field.
   */
  protected void addField(String name, String props) {
    m_fields.put(name, props);
  }

  /**
   * Create a table with this schema.
   * If the table already exists, does nothing.
   *
   * @param name The name of the table.
   *
   * @throws SQLException If an SQL error occurs.
   */
  public void createTable(String name) throws SQLException {
    Connection connection = m_database.getConnection();
    if (connection == null) {
      return;
    }

    // Build the update query.
    StringBuilder fields = new StringBuilder();
    fields.append("_id INTEGER PRIMARY KEY AUTOINCREMENT"); // Default primary key.
    for (Map.Entry<String, String> entry : m_fields.entrySet()) {
      String field = entry.getKey();
      String props = entry.getValue();
      fields.append(String.format(", %s %s", field, props));
    }
    String query = String.format("CREATE TABLE IF NOT EXISTS %s (%s)", name, fields.toString());

    // Run the update.
    PreparedStatement stmt = connection.prepareStatement(query);
    stmt.executeUpdate();
  }

  /**
   * Drop a table created with the schema.
   * If the table doesn't exist, does nothing.
   *
   * @param name The name of the table.

   * @throws SQLException If an SQL error occurs.
   */
  public void dropTable(String name) throws SQLException {
    Connection connection = m_database.getConnection();
    if (connection == null) {
      return;
    }

    // Run the update to drop the table.
    PreparedStatement stmt = connection.prepareStatement(
      String.format("DROP TABLE IF EXISTS %s", name)
    );
    stmt.executeUpdate();
  }

}
