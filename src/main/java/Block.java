import java.util.ArrayList;
import java.util.Date;

class Block {
    private String hash;
    private final String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    private final long timeStamp;
    private int nonce = 0;

    Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = computeHash();
    }

    String computeHash() {
        return StringUtil.computeSha256Hash(
            previousHash +
            Long.toString(timeStamp) +
            Integer.toString(nonce) +
            merkleRoot
        );
    }

    String getHash() {
        return hash;
    }

    String getPreviousHash() {
        return previousHash;
    }

    void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');

        while(!hash.substring(0, difficulty).equals(target)) {
            nonce ++;
            hash = computeHash();
        }
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        if (!previousHash.equals("0")) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction successfully added to block");
        return true;
    }
}
