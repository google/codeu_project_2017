package codeu.chat.database;

import java.sql.*;

import java.util.HashMap;
import java.util.Map;

public final class DBObject<S extends Schema> {

  private final Table<S> table;
  private int id;

  private Map<String, String> fields;

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
   * @return Whether the update was successful.
   */
  public boolean refresh() {
    // Find the updated object and copy the fields.
    DBObject<S> updated = table.find(id);
    if (updated == null) return false;
    this.fields = updated.fields;
    return true;
  }

  /**
   * Save the object.
   *
   * @return Whether the save was successful.
   */
  public boolean save() {
    return table.update(id, fields);
  }

  /**
   * Remove the object.
   * The object should no longer be used after this is called.
   *
   * @return Whether the removal was successful.
   */
  public boolean remove() {
    return table.remove(id);
  }

}
