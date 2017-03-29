package codeu.chat.database;

import java.sql.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codeu.chat.util.Logger;

public abstract class Table<S extends Schema> {

  private final static Logger.Log LOG = Logger.newLog(Table.class);

  private final S schema;
  private final Database database;
  private final String name;

  public Table(S schema, Database database, String name) {
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

  /**
   * Find objects with a given query.
   *
   * @param query The query.
   * @param values The values for the query.
   *
   * @return The list of DBObjects found.
   */
  public List<DBObject<S>> findQuery(String query, String... values) {
    Connection connection = database.getConnection();
    if (connection == null) return new ArrayList<DBObject<S>>();

    List<DBObject<S>> objects = new ArrayList<DBObject<S>>();

    // Run a query in the database.
    String sqlQuery = String.format("SELECT * FROM %s WHERE %s", name, query);
    try (PreparedStatement stmt = connection.prepareStatement(sqlQuery)) {
      int i = 1;
      for (String value : values) {
        stmt.setString(i ++, value);
      }
      try (ResultSet results = stmt.executeQuery()) {
        // Try to find the first row.
        while (results.next()) {
          // Build a map of field values.
          int id = results.getInt("_id");
          Map<String, String> fields = new HashMap<String, String>();
          for (String field : schema.getFields().keySet()) {
            fields.put(field, results.getString(field));
          }

          // Create a DBObject with the field values.
          DBObject<S> object = new DBObject<S>(this, id, fields);

          // Add the object to the list.
          objects.add(object);
        }
      }
    } catch (SQLException ex) {
      LOG.error("Failed to query database: ", ex.getMessage());
    }

    // Return the found objects.
    return objects;
  }

  /**
   * Find objects with given fields and values.
   *
   * @param parameters The field and value pairs.
   *
   * @return The list of DBObjects found.
   */
  public List<DBObject<S>> find(Map<String, String> parameters) {
    // Build the query.
    StringBuilder query = new StringBuilder();
    int numFields = parameters.size();
    for (String field : parameters.keySet()) {
      query.append(String.format("%s = ?", field));
      if (-- numFields > 0) {
        query.append(" AND ");
      }
    }

    return findQuery(query.toString(), parameters.values().toArray(new String[parameters.size()]));
  }

  /**
   * Find objects with given fields and values.
   *
   * @param pairs The field and value pairs.
   *
   * @return The list of DBObjects found.
   */
  public List<DBObject<S>> find(String... pairs) {
    Map<String, String> fields = new HashMap<String, String>();
    for (int i = 0; i < pairs.length; i += 2) {
      fields.put(pairs[i], pairs[i + 1]);
    }
    return find(fields);
  }

  /**
   * Find an object in the database with a given id.
   *
   * @param id The ID.
   *
   * @return The DBObject if found, null otherwise.
   */
  public DBObject<S> find(int id) {
    List<DBObject<S>> found = find("_id", Integer.toString(id));
    if (found.size() > 0) {
      return found.get(0);
    } else {
      return null;
    }
  }

  /**
   * Create a new object in the database.
   *
   * @param fields The fields to use.
   *
   * @return The ID of the new object if successful, -1 otherwise.
   */
  public int create(Map<String, String> fields) {
    Connection connection = database.getConnection();
    if (connection == null) return -1;

    // Get the field names and values.
    StringBuilder names = new StringBuilder();
    StringBuilder values = new StringBuilder();
    int numFields = fields.size();
    for (String field : fields.keySet()) {
      names.append(field);
      values.append("?");
      if (-- numFields > 0) {
        names.append(", ");
        values.append(", ");
      }
    }

	  // Run an update to create the object.
    String query = String.format(
      "INSERT INTO %s (%s) VALUES (%s)",
      name, names.toString(), values.toString());
    try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      int i = 1;
      for (String value : fields.values()) {
        stmt.setString(i ++, value);
      }
      stmt.executeUpdate();
      try (ResultSet results = stmt.getGeneratedKeys()) {
        return results.getInt(1);
      }
    } catch (SQLException ex) {
      LOG.error("Failed to update database: ", ex.getMessage());
    }

    return -1;
  }

  /**
   * Update an object in the database.
   *
   * @param id The ID of the object to update.
   * @param fields The fields to update.
   *
   * @return Whether the update was successful.
   */
  public boolean update(int id, Map<String, String> fields) {
    Connection connection = database.getConnection();
    if (connection == null) return false;

    // Build the query.
    StringBuilder updates = new StringBuilder();
    int numFields = fields.size();
    for (String field : fields.keySet()) {
      updates.append(String.format("%s = ?", field));
      if (-- numFields > 0) {
        updates.append(", ");
      }
    }

    // Run the query to update the object.
    String query = String.format("UPDATE %s SET %s WHERE _id = ?", name, updates.toString());
    System.out.println(query);
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      int i = 1;
      for (String value : fields.values()) {
        stmt.setString(i ++, value);
      }
      stmt.setInt(i, id);
      stmt.executeUpdate();
      return true;
    } catch (SQLException ex) {
      LOG.error("Failed to update database: ", ex.getMessage());
    }

    return false;
  }

  /**
   * Remove an object from the database.
   *
   * @param id The ID of the object to remove.
   *
   * @return Whether the removal was successful.
   */
  public boolean remove(int id) {
    Connection connection = database.getConnection();
    if (connection == null) return false;

    // Run the query to remove the object.
    String query = String.format("DELETE FROM %s WHERE _id = ?", name);
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, id);
      stmt.executeUpdate();
      return true;
    } catch (SQLException ex) {
      LOG.error("Failed to update database: ", ex.getMessage());
    }

    return false;
  }

}
