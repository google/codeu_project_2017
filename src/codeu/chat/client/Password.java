package codeu.chat.client;

/**
 * Created by Kinini on 3/13/17.
 */
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.security.SecureRandom;
import java.util.Random;
import java.security.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
import javax.crypto.*;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.math.BigInteger;
import java.lang.*;

import codeu.chat.client.ClientUser;
import codeu.chat.util.store.Store;
import codeu.chat.common.User;

public class Password {
    private static final int ITERATIONS = 10000;
    private static final int SALT_LENGTH = 16;
    private static final int DESIRED_KEYLEN = 256;
    private static final int MAX_TRIALS=3;
    private static final Random RANDOM = new SecureRandom();
    private static Map<String, String> DB = new HashMap<String, String>();



    public static void createPassword(String user, String password, Store<String, String> passwordDB){

            try {
                encryptPassword(passwordDB, user, password, getSaltvalue());
            } catch (NoSuchAlgorithmException e) {
                System.out.println(e.getMessage());
            } catch (InvalidKeySpecException e) {
                System.out.println(e.getMessage());
            }

    }


    public static String promptForPassword(String name){
        Console console = System.console();
        while(true){
            String password = new String(console.readPassword("Enter Password: "));
            String confirmPassword = new String(console.readPassword("Confirm Password: "));
            final boolean validPass = password.length()!=0 && password.equals(confirmPassword);
            password = validPass ? password : null;
            if (password == null) {
                System.out.format("Password not created - %s.\n", validPass ? "server failure" : "Passwords don't match. Try Again!");
            }
            else {
                System.out.println("Password Strength: "+ passwordStrength(password));
                ClientUser.passwordRecoveryDB.insert(name, collectPasswordRecoveryInfo(name));
                return password;
            }
        }
    }
    public static boolean authenticateUserCommandline(String name, Store<String, String> passwordDB){
        Console console = System.console();
        int i=0;
        boolean correctPass=false;
        String password = new String(console.readPassword("Enter Password: "));
        try{
            while(true) {
                correctPass = verifyPassword(passwordDB, name, password);
                // System.out.println(correctPass);
                if (correctPass)
                    break;
                if(i==MAX_TRIALS) {
                    System.out.println("Error: Sign in failed, invalid password");
                    changePassword(name);
                    break;
                }
                password = String.valueOf((console.readPassword("Try again: ")));
                i++;
            }
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.out.println(e.getMessage());
        }
        return correctPass;
    }

    private static void changePassword(String name) {
        System.out.println("Forgotten password? Y N");
        Scanner input = new Scanner(System.in);
        String choice=input.nextLine();
        if (choice.equals("Y") || choice.equals("y")) {
            if (ClientUser.passwordRecoveryDB.first(name).equals(collectPasswordRecoveryInfo(name))) {
                String newPassword = promptForPassword(name);
                //delete old passwords when Store implements delete
                createPassword(name, newPassword, ClientUser.passwordsDB);
                System.out.println("Password changed. Try signing in again");
            }
            else{
                System.out.println("Error: Unable to recover password");
            }
        }
        else{
            System.out.println("Error: Sign in failed, invalid password");
        }
    }

    public static boolean authenticateUserGUI(String user, String password){
        boolean isCorrect=false;
        try{
            isCorrect=verifyPassword(ClientUser.passwordsDB, user, password);
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        } catch (InvalidKeySpecException e) {
            System.out.println(e.getMessage());
        }
        return isCorrect;
    }

    public static final boolean verifyPassword(Store<String, String> passwordDB, String username, String password)throws NoSuchAlgorithmException, InvalidKeySpecException{
        String[] stored_pass=passwordDB.first(username).split("\\$");
        int iterations=Integer.parseInt(stored_pass[0]);
        byte[] salt=convertToBytes(stored_pass[1]);
        byte[] hash=convertToBytes(stored_pass[2]);

        byte[] hash_of_input =hash(password, salt);

        /*use bit manipulation to compare both hashes(xor the lengths of both(should give 0),
         OR this with, the xor of each corresponding byte element which should give you 0)
          */

        int difference=hash.length ^ hash_of_input.length;
        for(int i=0; i< hash.length && i< hash_of_input.length; i++){
            difference|=hash[i]^hash_of_input[i];
        }
        return difference==0;
    }


    public static final void encryptPassword( Store<String, String> passwordDB, String username, String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String encrypted_pass = ITERATIONS + "$" + convertToHex(salt) + "$" + convertToHex(hash(password, salt));

        if (encrypted_pass == null) throw new NoSuchAlgorithmException();

        //DB.put(username, encrypted_pass);
        passwordDB.insert(username, encrypted_pass);
    }

    private static final byte[] hash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, DESIRED_KEYLEN);
        SecretKeyFactory secret = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey key = secret.generateSecret(spec);
        if (key == null) throw new InvalidKeySpecException();
        byte[] hash = key.getEncoded();
        return hash;
    }

    private static final byte[] getSaltvalue() throws NoSuchAlgorithmException {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.nextBytes(salt);
        return salt;
    }


    private static String convertToHex(byte[] arr) throws NoSuchAlgorithmException {
        BigInteger bigInt = new BigInteger(1, arr);
        String hex = bigInt.toString(16);
        int padding = (arr.length * 2) - hex.length();
        if (padding > 0) return String.format("%0" + padding + "d", 0) + hex;
        return hex;
    }

    private static byte[] convertToBytes(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static final String passwordStrength(String password){

        //a very strong password has special, lowercase, uppercase, digit characters, now hitespace and has length of at least 8
        if(password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}")) return "Very Strong!";

        //a strong password has a lowercase, uppercase and either a digit or special character and has at least 6 characters
        if(password.matches("(?=.*[a-z])(?=.*[A-Z])((?=.*[0-9])|(?=.*[@#$%^&+=])).{6,}")) return "Strong!";

        //a medium strength password has a length of at least 6 and has both lower and uppercase characters.
        if(password.matches("(?=.*[a-z])(?=.*[A-Z]).{6,}")) return "Medium!";

        //the rest are weak passwords
        return "Weak!";
        }

    public static final String collectPasswordRecoveryInfo(String name){
        System.out.println("Choose one security question: ");
        System.out.println("1 : What is the name of your elementary school?");
        System.out.println("2: What is the name of your pet?");
        System.out.println("3. Which city did you meet your spouse?");

        Scanner input=new Scanner(System.in);
        int question=input.nextInt();

        System.out.println("Answer: ");
        Scanner scanner=new Scanner(System.in);
        String answer=scanner.nextLine();

        return String.valueOf(input) + "$" + answer;

    }

    //using username temporarily to store recovery passwords instead of UID

}


