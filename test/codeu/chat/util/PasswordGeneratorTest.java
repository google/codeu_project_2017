package codeu.chat.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * This tests that the password hashes and salts PasswordGeneratorerated can be
 * used correctly for password protection. Note that a more extensive testing
 * may be required.
 *
 * @author Lauren
 *
 */
public class PasswordGeneratorTest {

  /**
   * Test that the PasswordGeneratorerate salt is "random".
   */
  @Test
  public void testGetSalt() {
    Set<byte[]> list = new HashSet<>();
    for (int i = 0; i < 100000; i++) {
      byte[] salt = PasswordGenerator.getSalt();
      if (list.contains(salt)) {
        assertTrue(false);
      }
      list.add(salt);
    }
  }

  /**
   * Tests that the hash can create a hashed password from a raw one.
   */
  @Test
  public void testHashPassword() {
    byte[] password = PasswordGenerator.hash("password".toCharArray(),
        PasswordGenerator.getSalt());
    assertNotNull(password);
  }

  /**
   * Tests that the password hashed with the same salt is the same.
   */
  @Test
  public void testMatchingPassword() {
    byte[] salt = PasswordGenerator.getSalt();
    byte[] password1 = PasswordGenerator.hash("password".toCharArray(),
        salt);
    byte[] password2 = PasswordGenerator.hash("password".toCharArray(),
        salt);
    byte[] password3 = PasswordGenerator.hash("password".toCharArray(),
        PasswordGenerator.getSalt());
    for (int i = 0; i < password2.length; i++) {
      assertTrue(password1[i] == password2[i]);
    }
    boolean bool = true;
    for (int i = 0; i < password3.length; i++) {
      if (password1[i] != password3[i]) {
        bool = false;
      }
    }
    assertFalse(bool);
    bool = true;
    for (int i = 0; i < password3.length; i++) {
      if (password2[i] != password3[i]) {
        bool = false;
      }
    }
    assertFalse(bool);
  }

  /**
   * Test that the password hashed with the same salt is found to actually be
   * the correct password.
   */
  @Test
  public void testValidatePasswordSameSalt() {
    String password = "password";
    byte[] salt = PasswordGenerator.getSalt();
    byte[] expectedPassword = PasswordGenerator
        .hash("password".toCharArray(), salt);
    byte[] notExpectedPassword = PasswordGenerator
        .hash("lassword".toCharArray(), salt);
    assertTrue(PasswordGenerator.validatePassword(password, salt,
        expectedPassword));
    assertFalse(PasswordGenerator.validatePassword(password, salt,
        notExpectedPassword));

  }

  /**
   * Test that the password hashed with a different salt is not found to
   * actually be the correct password.
   */
  @Test
  public void testValidatePasswordDifferentSalt() {
    String password = "password";
    byte[] salt = PasswordGenerator.getSalt();
    byte[] expectedPassword = PasswordGenerator
        .hash("password".toCharArray(), salt);
    byte[] notExpectedPassword = PasswordGenerator
        .hash("password".toCharArray(), PasswordGenerator.getSalt());
    assertTrue(PasswordGenerator.validatePassword(password, salt,
        expectedPassword));
    assertFalse(PasswordGenerator.validatePassword(password, salt,
        notExpectedPassword));
  }

  /**
   * Tests that the password hash is unique. This takes some time to compute,
   * but it has been tested with loops up to 1000.
   */
  @Test
  public void testSamePasswordDifferentSalt() {
    Set<byte[]> list = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      byte[] salt = PasswordGenerator.getSalt();
      byte[] password1 = PasswordGenerator.hash("password".toCharArray(),
          salt);
      if (list.contains(password1)) {
        assertTrue(false);
      }
      list.add(password1);
    }
  }

  /**
   * Tests that with the same salt, a different password is not going to be
   * hashed to the same hash password.
   */
  @Test
  public void testDifferentPasswordSameSalt() {
    byte[] salt = PasswordGenerator.getSalt();
    StringBuilder str = new StringBuilder();
    str.append("passwor");
    Set<byte[]> list = new HashSet<>();
    for (int i = 97; i <= 122; i++) {
      char a = (char) i;
      str.append(a);
      byte[] password1 = PasswordGenerator
          .hash(str.toString().toCharArray(), salt);
      if (list.contains(password1)) {
        assertTrue(false);
      }
      list.add(password1);
      str.deleteCharAt(7);
    }
  }
}
