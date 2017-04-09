package codeu.chat.server.database;

import java.sql.SQLException;

import codeu.chat.database.Database;
import codeu.chat.database.Table;

/**
 * Table for storing user data.
 */
public class UserTable extends Table<UserSchema> {

  public UserTable(Database database) throws SQLException {
    super(new UserSchema(), database, "users");
  }

}
