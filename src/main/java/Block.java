import java.math.BigInteger;
import java.util.Date;

public class Block {
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce = 0;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = computeHash();
    }

    public String computeHash() {
        return StringUtil.computeSha256Hash(
            previousHash +
            Long.toString(timeStamp) +
            Integer.toString(nonce) +
            data
        );
    }

    public String getHash() {
        return hash;
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');

        while(!hash.substring(0, difficulty).equals(target)) {
            nonce ++;
            hash = computeHash();
        }
    }
}
