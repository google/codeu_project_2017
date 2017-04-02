package codeu.chat.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.util.Logger;

public abstract class Schema {

  private final static Logger.Log LOG = Logger.newLog(Schema.class);

  private final Map<String, String> fields;

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
   * Get the fields in the schema.
   *
   * @return The fields.
   */
  public Map<String, String> getFields() {
    return fields;
  }

  /**
   * Create a table with this schema.
   * If the table already exists, does nothing.
   *
   * @param name The name of the table.
   * @param database The database to create the table in.
   *
   * @return Whether the table exists after running the update.
   */
  public boolean createTable(String name, Database database) {
    Connection connection = database.getConnection();
    if (connection == null) {
      return false;
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
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.executeUpdate();
      return true;
    } catch (SQLException ex) {
      LOG.error("Failed to create table: ", ex.getMessage());
    }
    return false;
  }

  /**
   * Drop a table created with the schema.
   * If the table doesn't exist, does nothing.
   *
   * @param name The name of the table.
   * @param database The database to drop the table from.
   *
   * @return Whether the table no longer exists after the update.
   */
  public boolean dropTable(String name, Database database) {
    Connection connection = database.getConnection();
    if (connection == null) {
      return false;
    }

    // Run the update to drop the table.
    String query = String.format("DROP TABLE IF EXISTS %s", name);
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.executeUpdate();
      return true;
    } catch (SQLException ex) {
      LOG.error("Failed to drop table: ", ex.getMessage());
    }
    return false;
  }

}
