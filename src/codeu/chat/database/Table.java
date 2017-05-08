package codeu.chat.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codeu.chat.util.Logger;

/**
 * Represents a table in a database.
 */
public abstract class Table<S extends Schema> {

  private final static Logger.Log LOG = Logger.newLog(Table.class);

  private final S schema;
  private final Database database;
  private final String name;

  /**
   * Creates a table with a given schema in the given database with a given name.
   *
   * @param schema The schema to use.
   * @param database The database to use.
   * @param name The name to use.
   *
   * @throws SQLException If an SQL error occurs.
   */
  public Table(S schema, Database database, String name) throws SQLException {
    this.schema = schema;
    this.database = database;
    this.name = name;

    // Create the table.
    schema.createTable(name, database);
  }

  /**
   * Destroy the table.
   *
   * @throws SQLException If a SQL error occurs.
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
   * Get the name of the table.
   *
   * @return The name of the table.
   */
  public String getName() {
    return name;
  }

  /**
   * Find objects with a given query.
   *
   * @param query The query.
   * @param values The values for the query.
   *
   * @return The list of DBObjects found.
   *
   * @throws SQLException If a SQL error occurs.
   */
  public List<DBObject<S>> findQuery(String query, String... values) throws SQLException {
    Connection connection = database.getConnection();

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
      throw ex;
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
   *
   * @throws SQLException If a SQL error occurs.
   */
  public List<DBObject<S>> find(Map<String, String> parameters) throws SQLException {
    // Build the query.
    StringBuilder query = new StringBuilder();
    int numFields = parameters.size();
    for (String field : parameters.keySet()) {
      query.append(String.format("%s = ?", field));
      numFields --;
      if (numFields > 0) {
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
   *
   * @throws SQLException If a SQL error occurs.
   */
  public List<DBObject<S>> find(String... pairs) throws SQLException {
    // Make sure the size of the array is a multiple of 2.
    if (pairs.length % 2 == 1) {
      throw new IllegalArgumentException("Odd number of arguments given to find().");
    }

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
   *
   * @throws SQLException If a SQL error occurs.
   */
  public DBObject<S> find(int id) throws SQLException {
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
   *
   * @throws SQLException If a SQL error occurs.
   */
  public int create(Map<String, String> fields) throws SQLException {
    Connection connection = database.getConnection();

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
      throw ex;
    }
  }

  /**
   * Update an object in the database.
   *
   * @param id The ID of the object to update.
   * @param fields The fields to update.
   *
   * @throws SQLException If a SQL error occurs.
   */
  public void update(int id, Map<String, String> fields) throws SQLException {
    Connection connection = database.getConnection();

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
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      int i = 1;
      for (String value : fields.values()) {
        stmt.setString(i ++, value);
      }
      stmt.setInt(i, id);
      stmt.executeUpdate();
    } catch (SQLException ex) {
      LOG.error("Failed to update database: ", ex.getMessage());
      throw ex;
    }
  }

  /**
   * Remove an object from the database.
   *
   * @param id The ID of the object to remove.
   *
   * @throws SQLException If a SQL error occurs.
   */
  public void remove(int id) throws SQLException {
    Connection connection = database.getConnection();

    // Run the query to remove the object.
    String query = String.format("DELETE FROM %s WHERE _id = ?", name);
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, id);
      stmt.executeUpdate();
    } catch (SQLException ex) {
      LOG.error("Failed to update database: ", ex.getMessage());
      throw ex;
    }
  }

}
