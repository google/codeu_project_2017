import java.math.BigInteger;

public class PublicKey {
    BigInteger n, e;

    public PublicKey(BigInteger otherN, BigInteger otherE) {
        n = otherN;
        e = otherE;
    }

    public BigInteger get_n() {return n;}
    public BigInteger get_e() {return e;}
}