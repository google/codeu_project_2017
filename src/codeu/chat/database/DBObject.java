package codeu.chat.database;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a row with a unique ID in the database.
 * Each DBObject is associated with exactly one row.
 */
public final class DBObject<S extends Schema> {

  private final Table<S> table;
  private final int id;

  private Map<String, String> fields;

  /**
   * Creates a representation of a table row.
   *
   * @param table The table associated with the object.
   * @param id The unique ID of the row.
   * @param fields The fields of the row.
   */
  public DBObject(Table<S> table, int id, Map<String, String> fields) {
    this.table = table;
    this.id = id;
    this.fields = new HashMap<String, String>(fields);
  }

  /**
   * Get the value of a field.
   *
   * @param field The name of the field.
   *
   * @return The value associated with the field if it exists, null otherwise.
   */
  public String get(String field) {
    return fields.get(field);
  }

  /**
   * Set the value of a field.
   *
   * @param field The name of the field.
   * @param value The value of the field.
   */
  public void set(String field, String value) {
    // Make sure the field is valid.
    if (!fields.containsKey(field)) return;
    fields.put(field, value);
  }

  /**
   * Update the fields from the database.
   *
   * @throws SQLException If a SQL error occurs.
   */
  public void refresh() throws SQLException {
    // Find the updated object and copy the fields.
    DBObject<S> updated = table.find(id);
    if (updated == null) return;
    this.fields = updated.fields;
  }

  /**
   * Save the object.
   *
   * @throws SQLException If a SQL error occurs.
   */
  public void save() throws SQLException {
    table.update(id, fields);
  }

  /**
   * Remove the object.
   * The object should no longer be used after this is called.
   *
   * @throws SQLException If a SQL error occurs.
   */
  public void remove() throws SQLException {
    table.remove(id);
  }

}
