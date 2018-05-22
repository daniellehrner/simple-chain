import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;

public class SimpleChain {

    private static ArrayList<Block> blockchain = new ArrayList<>();
    private static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();

        System.out.println("Private and public keys:");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

        blockchain.add(new Block("I'm the genesis block", "0"));
        blockchain.get(0).mineBlock(difficulty);

        blockchain.add(new Block("I'm the second block", blockchain.get(blockchain.size()-1).getHash()));
        blockchain.get(1).mineBlock(difficulty);

        blockchain.add(new Block("I'm the third block", blockchain.get(blockchain.size()-1).getHash()));
        blockchain.get(2).mineBlock(difficulty);

        if (!isChainValid()) {
            System.out.println("Blockchain is not valid");
            return;
        }

        System.out.println("Blockchain is valid");

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);
    }

    public static Boolean isChainValid() {
        Block current, previous;

        for (int i = 1; i < blockchain.size(); ++i) {
            current = blockchain.get(i);
            previous = blockchain.get(i - 1);

            if (!current.getHash().equals(current.computeHash())) {
                System.out.println("Current hashes not equal");
                return false;
            }

            if (!previous.getHash().equals(previous.computeHash())) {
                System.out.println("Previous hashes not equal");
                return false;
            }
        }

        return true;
    }
}
