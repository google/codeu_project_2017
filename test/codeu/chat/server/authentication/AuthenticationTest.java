package codeu.chat.server.authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import codeu.chat.database.Database;
import codeu.chat.server.database.UserSchema;
import codeu.chat.authentication.AuthenticationCode;

public final class AuthenticationTest {

  private Database database;
  private UserSchema userSchema;
  private Authentication authentication;

  @Before
  public void setupDatabase() throws SQLException {
    database = new Database("test.db");
    userSchema = new UserSchema();
    userSchema.dropTable("users", database);
    authentication = new Authentication(database);
  }

  @Test
  public void testRegister() {
    int result;
    result = authentication.register("username", "password");
    assertEquals(result, AuthenticationCode.SUCCESS);
  }

  @Test
  public void testLogin() {
    int result;
    result = authentication.register("username", "password");
    assertEquals(result, AuthenticationCode.SUCCESS);
    result = authentication.login("username", "password");
    assertEquals(result, AuthenticationCode.SUCCESS);
  }

  @Test
  public void testUserExists() {
    int result;
    result = authentication.register("username", "password");
    assertEquals(result, AuthenticationCode.SUCCESS);
    result = authentication.register("username", "password");
    assertEquals(result, AuthenticationCode.REGISTER_USER_EXISTS);
  }

  @Test
  public void testUserDoesNotExist() {
    int result;
    result = authentication.login("username", "password");
    assertEquals(result, AuthenticationCode.LOGIN_FAILED);
  }

  @Test
  public void testUserWrongPassword() {
    int result;
    result = authentication.register("username", "password");
    assertEquals(result, AuthenticationCode.SUCCESS);
    result = authentication.login("username", "notpassword");
    assertEquals(result, AuthenticationCode.LOGIN_FAILED);
  }

}
