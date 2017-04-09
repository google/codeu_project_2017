package codeu.chat.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.util.Logger;

/**
 * Represents a database schema.
 */
public abstract class Schema {

  private static final Logger.Log LOG = Logger.newLog(Schema.class);

  private final Map<String, String> fields;

  /**
   * Creates a schema.
   */
  public Schema() {
    this.fields = new HashMap<String, String>();
  }

  /**
   * Add a field to the schema.
   *
   * @param name The name of the field.
   * @param props The properties associated with the field in SQL, such
   *              as "VARCHAR(255) NOT NULL".
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
   * @throws SQLException If a SQL error occurs.
   */
  public void createTable(String name, Database database) throws SQLException {
    Connection connection = database.getConnection();

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
    } catch (SQLException ex) {
      LOG.error(ex, "Failed to create table.");
	  throw ex;
    }
  }

  /**
   * Drop a table created with the schema.
   * If the table doesn't exist, does nothing.
   *
   * @param name The name of the table.
   * @param database The database to drop the table from.
   *
   * @throws SQLException If a SQL error occurs.
   */
  public void dropTable(String name, Database database) throws SQLException {
    Connection connection = database.getConnection();

    // Run the update to drop the table.
    String query = String.format("DROP TABLE IF EXISTS %s", name);
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.executeUpdate();
    } catch (SQLException ex) {
      LOG.error(ex, "Failed to drop table.");
	  throw ex;
    }
  }

}
