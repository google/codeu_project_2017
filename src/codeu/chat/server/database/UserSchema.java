package codeu.chat.server.database;

import codeu.chat.database.Database;
import codeu.chat.database.Schema;

/**
 * Schema for user data table.
 */
public class UserSchema extends Schema {

  public UserSchema() {
    addField("username", "TEXT(255)");
    addField("password", "TEXT");
    addField("salt", "VARCHAR(255)");
  }

}
