package codeu.chat.common;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Uuid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import static codeu.chat.util.Serializers.BYTES;

public class EncryptionKey {

    private BigInteger number, modulus;

    public EncryptionKey(BigInteger number, BigInteger modulus){
        this.number = number;
        this.modulus = modulus;
    }

    public void setNumber(BigInteger number) {
        this.number = number;
    }

    public BigInteger getNumber(){
        return number;
    }

    public BigInteger getModulus(){
            return modulus;
    }

    public String toString(){
        return ("number " + number + '\n' + "modulus " + modulus);
    }

}
