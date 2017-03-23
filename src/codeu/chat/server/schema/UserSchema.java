package codeu.chat.server.schema;

import codeu.chat.database.Database;
import codeu.chat.database.Schema;

public class UserSchema extends Schema {

  public UserSchema(Database database) {
    super(database);

    addField("username", "TEXT");
    addField("password", "TEXT");
    addField("salt", "VARCHAR(255)");
  }

}
