/**
 * Encryptor.java
 * Author: Arunabh Singh
 * Main code of encrypting the actual String sequences produced by compression code.
 */

import java.security.SecureRandom;
import java.math.BigInteger;

public class Encryptor {
    final int bitLength = 1024;
    private BigInteger privateKey;
    public PublicKey publicKey;

    public Encryptor() {
        newKeys();
    }

    public BigInteger encrypt(PublicKey key, String input) {
        BigInteger in = new BigInteger(input.getBytes());
        return in.modPow(key.get_e(), key.get_n());
    }

    public String decrypt(BigInteger cipher) {
        String res = new String(cipher.modPow(privateKey, publicKey.get_n()).toByteArray());
        return res;
    }

    private void newKeys() {
        SecureRandom rand = new SecureRandom();
        BigInteger p = new BigInteger(bitLength / 2, 63, rand);
        BigInteger q = new BigInteger(bitLength / 2, 63, rand);

        BigInteger n = p.multiply(q);

        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = new BigInteger("3");

        while (m.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }

        privateKey = e.modInverse(m);
        publicKey = new PublicKey(n, e);
    }
}