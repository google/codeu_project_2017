class Encryptor_Test {
    public static void main(String[] args) throws Exception {
        byte[] plaintext = "We are gonna ace this project!".getBytes();

        EncryptorKey BobsKey = new EncryptorKey();

        byte[] ciphertext = BobsKey.encrypt(plaintext);

        EncryptorKey AlicesKey = new EncryptorKey();

        byte[] output = AlicesKey.decrypt(ciphertext, BobsKey.getKey());

        System.out.println(new String(plaintext));
        System.out.println(new String(ciphertext));
        System.out.println(new String(output));

    }
}