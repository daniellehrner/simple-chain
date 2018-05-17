import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class App {

    private static ArrayList<Block> blockchain = new ArrayList<>();

    public static void main(String[] args) {
        blockchain.add(new Block("I'm the genesis block", "0"));
        blockchain.add(new Block("I'm the second block", blockchain.get(blockchain.size()-1).getHash()));
        blockchain.add(new Block("I'm the third block", blockchain.get(blockchain.size()-1).getHash()));

        if (!isChainValid()) {
            System.out.println("Blockchain is not valid");
            return;
        }

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
