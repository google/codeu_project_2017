package codeu.chat.common;
import java.math.BigInteger;
import java.security.SecureRandom;


public class RSA {

    private BigInteger d, e, N;

    // instance of decryption given persons public key
    public RSA(BigInteger given_N, BigInteger given_e, BigInteger given_d) {
        N = given_N;
        e = given_e;
        d = given_d;

    }

    // generate N by prime numbers p and q
    // generate d and e

    public RSA() {

        SecureRandom s = new SecureRandom();
        // generate random primes
        int bit_length = 512;
        BigInteger p = new BigInteger(bit_length, 100, s);
        BigInteger q = new BigInteger(bit_length, 100, s);
        N = p.multiply(q);

        //generate m = (p-1)(q-1)
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q
                .subtract(BigInteger.ONE));

        // generate e such that it is relatively prime to (p-1)(q-1)
        e = new BigInteger("3");
        while (m.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }

        d = e.modInverse(m);
    }

    // encrypt a string message
    public synchronized String encrypt(String message) {
        return (new BigInteger(message.getBytes())).modPow(e, N).toString();
    }

    // decrypt a string message
    public synchronized String decrypt(String message) {
        return new String((new BigInteger(message)).modPow(d, N).toByteArray());
    }

    public BigInteger getD() { return d;}
    public BigInteger getE() { return e;}
    public BigInteger getN() { return N;}

//    public static void main(String[] args) {
//        RSA test = new RSA();
//        String lol = "This is a test";
//        String new_lol = test.encrypt(lol);
//        System.out.println(new_lol);
//        String back = test.decrypt(new_lol);
//        System.out.println(back);
//    }


}
