package codeu.chat.common;

import java.security.*;
import java.math.*;

public class Password
{
    private Password(){};

    public static String createHash(String password, String salt)
    {
        String hash_num = null;

        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes());
            md.update(salt.getBytes());
            byte[] arr = md.digest();

            StringBuffer hexStr = new StringBuffer();
            for(int i = 0; i < arr.length; i++)
            {
                hexStr.append(Integer.toHexString(0xFF & arr[i]));
            }
            hash_num = hexStr.toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return hash_num;
    }

    public static String createSalt()
    {
        SecureRandom rand_num = new SecureRandom();
        return new BigInteger(130, rand_num).toString(32);
    }
}