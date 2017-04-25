/**
 * EncryptorKey.java
 * Represents user security data to be attached to each account.
 * Uses AES encryption.
 */

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptorKey {
    /**
     * EncryptorKey
     * Initializes a key and cipher to prepare for symmetric-key encryption.
     * @throws Exception allows for KeyGenerator exceptions.
     */
    public EncryptorKey() throws Exception {
        KeyGenerator keyMaker = KeyGenerator.getInstance("AES");
        keyMaker.init(128);

        key = keyMaker.generateKey();
        cipher = Cipher.getInstance("AES");
    }

    /**
     * encrypt
     * Takes in a byte array and encrypts it into an ecrypted byte array.
     * @param input byte array to encrypt
     * @param keyToEncrypt key used for encryption
     * @return encrypted byte array
     * @throws Exception
     */
    public byte[] encrypt(byte[] input, SecretKey keyToEncrypt) throws Exception {
        cipher.init(cipher.ENCRYPT_MODE, keyToEncrypt);
        return cipher.doFinal(input);
    }

    /**
     * encrypt
     * Takes in a byte array and encrypts it into an ecrypted byte array.
     * Default version.
     * @param input byte array to encrypt
     * @return encrypted byte array
     * @throws Exception
     */
    public byte[] encrypt(byte[] input) throws Exception {
        cipher.init(cipher.ENCRYPT_MODE, getKey());
        return cipher.doFinal(input);
    }

    /**
     * decrypt
     * Decrypts a previously encrypted byte array.
     * @param input encrypted byte array
     * @param keyToDecrypt key to use for decryption
     * @return the plaintext yielded from the byte array
     * @throws Exception
     */
    public byte[] decrypt(byte[] input, SecretKey keyToDecrypt) throws Exception {
        cipher.init(cipher.DECRYPT_MODE, keyToDecrypt);
        return cipher.doFinal(input);
    }

    /**
     * decrypt
     * Decrypts a previously encrypted byte array. Default version
     * @param input encrypted byte array
     * @return the plaintext yielded from the byte array
     * @throws Exception
     */
    public byte[] decrypt(byte[] input) throws Exception {
        cipher.init(cipher.DECRYPT_MODE, getKey());
        return cipher.doFinal(input);
    }

    /**
     * getKey
     * Returns the key.
     * @return the key that can encrypt/decrypt
     */
    public SecretKey getKey() {
        return key;
    }

    // AES key
    private SecretKey key;
    private Cipher cipher;
}
