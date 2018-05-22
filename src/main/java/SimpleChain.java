import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;

class SimpleChain {

    private static final ArrayList<Block> blockchain = new ArrayList<>();
    private static final int DIFFICULTY = 5;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        System.out.println("Private and public keys:");
        System.out.println(walletA.getPrivateKeyString());
        System.out.println(walletA.getPublicKeyString());

        Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
        transaction.sign(walletA.getPrivateKey());

        if (!transaction.verify()) {
            System.out.println("Transaction could not be verified");
        }

        System.out.println("Transaction is successfully verified");
    }

    private static Boolean isChainValid() {
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
