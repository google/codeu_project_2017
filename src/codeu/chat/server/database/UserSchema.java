package codeu.chat.server.database;

import codeu.chat.database.Database;
import codeu.chat.database.Schema;

public class UserSchema extends Schema {

  public UserSchema() {
    addField("username", "TEXT");
    addField("password", "TEXT");
    addField("salt", "VARCHAR(255)");
  }

}
