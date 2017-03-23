package codeu.chat.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public abstract class Schema {

  Map<String, String> fields;

  public Schema() {
    this.fields = new HashMap<String, String>();
  }

  /**
   * Add a field to the schema.
   *
   * @param name The name of the field.
   * @param props The properties associated with the field.
   */
  protected void addField(String name, String props) {
    fields.put(name, props);
  }

  /**
   * Create a table with this schema.
   * If the table already exists, does nothing.
   *
   * @param name The name of the table.
   * @param database The database to create the table in.
   *
   * @throws SQLException If an SQL error occurs.
   */
  public void createTable(String name, Database database) throws SQLException {
    Connection connection = database.getConnection();
    if (connection == null) {
      return;
    }

    // Build the update query.
    StringBuilder fields = new StringBuilder();
    fields.append("_id INTEGER PRIMARY KEY AUTOINCREMENT"); // Default primary key.
    for (Map.Entry<String, String> entry : this.fields.entrySet()) {
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
   * @param database The database to drop the table from.

   * @throws SQLException If an SQL error occurs.
   */
  public void dropTable(String name, Database database) throws SQLException {
    Connection connection = database.getConnection();
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
