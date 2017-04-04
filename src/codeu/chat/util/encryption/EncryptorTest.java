import java.math.BigInteger;

public class EncryptorTest {
    public static void main(String[] args) {
        Encryptor lock = new Encryptor();

        String input = "We're going to ace this project.";
        System.out.println("Input: " + input);
        BigInteger cipher = lock.encrypt(lock.publicKey, input);
        System.out.println("Cipher: " + cipher.toString());
        String output = lock.decrypt(cipher);
        System.out.println("Output: " + output);

        Person Alice = new Person();
        Person Bob = new Person();

        BigInteger secret = Alice.encrypt(Bob.getPublicKey(), "I love Bob.");
        System.out.println("\nBob sees: " + Bob.decrypt(secret));
        System.out.println("Alice sees: " + Alice.decrypt(secret));
    }
}

class Person {
    public Encryptor crypt;

    public Person() {
        crypt = new Encryptor();
    }

    public BigInteger encrypt(PublicKey publicKey, String input) {
        return crypt.encrypt(publicKey, input);
    }

    public String decrypt(BigInteger cipher) {
        return crypt.decrypt(cipher);
    }

    public PublicKey getPublicKey() {
        return crypt.publicKey;
    }
}