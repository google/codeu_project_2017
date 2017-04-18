package codeu.chat.authentication;

public final class AuthenticationCode {

  public static final int
    SUCCESS                = 0,
    REGISTER_USER_EXISTS   = 1,
    REGISTER_INVALID_INPUT = 2,
    LOGIN_FAILED           = 3,
    DB_ERROR               = 4,
    UNKNOWN                = 5;

}
