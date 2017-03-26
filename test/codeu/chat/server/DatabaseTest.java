package codeu.chat.server;

import static org.junit.Assert.assertTrue;

import java.util.UUID;
import org.junit.Test;


public final class DatabaseTest {

  private static final DBConnector.Connector con = new DBConnector.Connector();

  @Test
  public void testAddUser() {
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    boolean wasSuccessful = con.addAccount(randomUN, randomPW);
    assertTrue(wasSuccessful);
  }

  @Test
  public void testVerifyUser() {
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    con.addAccount(randomUN, randomPW);
    boolean wasSuccessful = con.verifyAccount(randomUN, randomPW);
    assertTrue(wasSuccessful);
  }
}
