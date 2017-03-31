package codeu.chat.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;
import org.junit.Test;


public final class DatabaseTest {

  private static final DBConnector.Connector con = new DBConnector.Connector();

  @Test
  public void testAddUser() {
    System.out.println("DATABASE TEST RUN");
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    assertTrue(con.addAccount(randomUN, randomPW));
  }

  @Test
  public void testVerifyUser() {
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    con.addAccount(randomUN, randomPW);
    assertTrue(con.verifyAccount(randomUN, randomPW));
    assertFalse(con.verifyAccount(randomUN, randomPW + "1"));
  }

  @Test
  public void testDeleteUser() {
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    con.addAccount(randomUN, randomPW);
    assertTrue(con.deleteAccount(randomUN));
    assertFalse(con.verifyAccount(randomUN, randomPW));
    assertTrue(con.addAccount(randomUN, randomPW));
  }

  @Test
  public void testUpdatePassword() {
    //
  }
}
