package codeu.chat.common;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.nio.charset.Charset;


public class RSA{
    private static final Charset CHARSET = Charset.forName("UTF-8"); //or any encoding
    private static BigInteger modulus;
    //This will be used as the modulus for both keys and defines de length of the keys.
    private static EncryptionKey pubKey, secKey;

    /**
     * Algorithm to create the public and secret keys to encrypt and decrypt a message based on
     * prime numbers and modular algebra using BigInteger.
     *
     * @param nBits the bit-length for the keys. 1024 or 2048 will provide enough security
     *              and should work for long messages and small files.
     * @see BigInteger
     */
    public static void generateKeys(int nBits){
        BigInteger prime1, prime2, phi, secretNumber, publicNumber;
        SecureRandom random = new SecureRandom();
        prime1 = BigInteger.probablePrime(nBits, random);
        prime2 = BigInteger.probablePrime(nBits, random);
        modulus = prime1.multiply(prime2);
        phi = prime1.subtract(BigInteger.ONE).multiply(prime2.subtract(BigInteger.ONE));
        publicNumber = BigInteger.probablePrime(nBits, random); //first part of the Public EncryptionKey
        secretNumber = publicNumber.modInverse(phi); // first part of the Secret EncryptionKey
        pubKey = new EncryptionKey(publicNumber, modulus);
        secKey = new EncryptionKey(secretNumber, modulus);
    }

     /**
     * Static method to encrypt messages
     *
     * @param message the original message to be encrypted
     * @param pubKey  the public key of the recipient used to encrypt the message
     *
     * @see BigInteger
     */
    public static BigInteger encrypt(BigInteger message, EncryptionKey pubKey){

        return message.modPow(pubKey.getNumber(), pubKey.getModulus());

    }

     /**
     * Static method to encrypt messages
     *
     * @param encryptedMessage the encrypted message to be decrypted
     * @param secKey  the private key of the recipient used to decrypt the message
     *
     * @see BigInteger
     */
    public static BigInteger decrypt(BigInteger encryptedMessage, EncryptionKey secKey){

        return encryptedMessage.modPow(secKey.getNumber(), secKey.getModulus());

    }
    
     /**
     * Static methods used to encode files and messages into BigIntegers for the RSA algorithm
     *
     * @param  message   String with the text of the message
     * @param  byteArray a byteArray representing any data type or files 
     * @return BIMessage the original content of the message now encoded in BigInteger so it can
     *                   be used in the RSA algorithm
     * @see BigInteger
     */
    public static BigInteger messageToBigInteger(String message){
        byte[] bytes = message.getBytes();
        BigInteger BIMessage =  new BigInteger(1, bytes);
        //BigInteger BIMessage = new BigInteger(message);
        return BIMessage;
    }

    public static BigInteger keyToBigInteger(String message){
        BigInteger BIMessage = new BigInteger(message);
        return BIMessage;
    }

    public static BigInteger toBigInteger(byte[] byteArray){
         BigInteger BIMessage =  new BigInteger(1, byteArray);
         return BIMessage;
    }

    /**
     * Static methods used to decode BigIntegers into the original messages or files
     *
     * @param  BIMessage BigInteger representing the message
     * @return bytes     a byteArray representing the original data type or files decoded 
     * @return message   a String with the original message decoded 
     * @see BigInteger
     */
    public static String messageToString(BigInteger BIMessage){
       byte[] bytes = BIMessage.toByteArray();
       String message = new String(bytes);
       return message;
    }

    public static String keyToString(BigInteger BIMessage){
        String message = String.valueOf(BIMessage);
        return message;
    }

    public static byte[] toByteArray (BigInteger BIMessage){
       byte[] bytes = BIMessage.toByteArray();
       return bytes;
    }

    public EncryptionKey getPubKey(){

        return pubKey;

    }

    public EncryptionKey getSecKey(){

        return secKey;

    }


}
