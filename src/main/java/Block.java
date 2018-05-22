import java.util.Date;

class Block {
    private String hash;
    private final String previousHash;
    private final String data;
    private final long timeStamp;
    private int nonce = 0;

    Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = computeHash();
    }

    String computeHash() {
        return StringUtil.computeSha256Hash(
            previousHash +
            Long.toString(timeStamp) +
            Integer.toString(nonce) +
            data
        );
    }

    String getHash() {
        return hash;
    }

    void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');

        while(!hash.substring(0, difficulty).equals(target)) {
            nonce ++;
            hash = computeHash();
        }
    }
}
