package codeu.chat.common;

import java.math.BigInteger;

public class Key{
    private BigInteger number, modulus;

    public Key(BigInteger number, BigInteger modulus){
        this.number = number;
        this.modulus = modulus;
    }

    public BigInteger getNumber(){
        return number;
    }

    public BigInteger getModulus(){
            return modulus;
    }

    @Override
    public String toString(){
        return "(" + number + "," + modulus + ")";
    }
}
