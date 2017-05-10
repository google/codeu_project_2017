package codeu.chat.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Password Generator that can create salts, encrypt passwords, and decrypt them.
 *
 * @author Lauren
 *
 */
public final class PasswordGenerator {

  private static final SecureRandom random = new SecureRandom();
  private static final int ITERATIONS = 10000;
  private static final int KEY_LENGTH = 256;

  /**
   * Create the instance of the SecureRandom to use.
   */
  private PasswordGenerator() {
  }

  /**
   * Generate some salt for password security.
   *
   * @return byte[]
   */
  public static byte[] getSalt() {
    byte[] salt = new byte[64];
    random.nextBytes(salt);
    return salt;
  }

  /**
   * Create the hashed password.
   *
   * @param password
   *          char[]
   * @param salt
   *          byte[]
   * @return byte[]
   */
  public static byte[] hash(char[] password, byte[] salt) {
    PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS,
        KEY_LENGTH);
    Arrays.fill(password, Character.MIN_VALUE);
    try {
      SecretKeyFactory skf = SecretKeyFactory
          .getInstance("PBKDF2WithHmacSHA256");
      return skf.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new AssertionError("Error while hashing a password ");
    } finally {
      spec.clearPassword();
    }
  }

  /**
   * Validate the password given a salt.
   *
   * @param password
   *          String
   * @param salt
   *          byte[]
   * @param expectedPassword
   *          byte[]
   * @return boolean
   */
  public static boolean validatePassword(String password, byte[] salt,
      byte[] expectedPassword) {
    char[] bytePassword = password.toCharArray();
    byte[] hashedPassword = hash(bytePassword, salt);
    password = null;
    Arrays.fill(bytePassword, Character.MIN_VALUE);
    if (hashedPassword.length != expectedPassword.length) {
      return false;
    }
    for (int i = 0; i < expectedPassword.length; i++) {
      if (hashedPassword[i] != expectedPassword[i]) {
        return false;
      }
    }
    return true;
  }
}
