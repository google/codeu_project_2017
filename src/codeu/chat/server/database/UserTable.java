package codeu.chat.server.database;

import codeu.chat.database.Database;
import codeu.chat.database.Table;

public class UserTable extends Table<UserSchema> {

  public UserTable(Database database) {
    super(new UserSchema(), database, "users");
  }

}
