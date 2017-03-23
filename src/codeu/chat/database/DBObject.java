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
   * Update the fields from the database.
   */
  void refresh() {
  }

}
