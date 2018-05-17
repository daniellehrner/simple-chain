import java.util.Date;

public class Block {
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = computeHash();
    }

    public String computeHash() {
        return StringUtil.computeSha256Hash(previousHash + Long.toString(timeStamp) + data);
    }

    public String getHash() {
        return hash;
    }
}
