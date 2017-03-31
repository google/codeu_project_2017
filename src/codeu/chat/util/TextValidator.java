package codeu.chat.util;

import java.util.regex.Pattern;

/**
 * Class used to determine whether or not certain inputs are valid for different fields throughout
 * the program
 */
public final class TextValidator {

  private static final Pattern validUserNamePattern = Pattern.compile("[a-zA-Z0-9]+");

  public static boolean isValidUserName(String username) {
    if (username == null) {
      return false;
    } else if (username.length() != 0 && validUserNamePattern.matcher(username).matches()) {
      return true;
    } else {
      return false;
    }
  }
}