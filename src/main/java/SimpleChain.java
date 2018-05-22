import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

class SimpleChain {

    private static final ArrayList<Block> blockchain = new ArrayList<>();
    private static HashMap<String, TransactionOutput> utxos = new HashMap<>();
    private static final int DIFFICULTY = 5;
    private static float MINIMUM_TRANSACTION = 0.1f;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinbase = new Wallet();

        genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
        genesisTransaction.sign(coinbase.getPrivateKey());
        genesisTransaction.setTransactionId("0");
        genesisTransaction.addTransactionOutput(
            new TransactionOutput(
                genesisTransaction.getTo(),
                genesisTransaction.getValue(),
                genesisTransaction.getTransactionId()
            )
        );
        utxos.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();
    }

    private static void addBlock(Block block) {
        block.mineBlock(DIFFICULTY);
        blockchain.add(block);
    }

    public static HashMap<String, TransactionOutput> getUtxos() {
        return utxos;
    }

    public static float getMinimumTransaction() {
        return MINIMUM_TRANSACTION;
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[DIFFICULTY]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.getHash().equals(currentBlock.computeHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.getHash().substring( 0, DIFFICULTY).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop through all blockchain transactions:
            TransactionOutput tempOutput;
            for (int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verify()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputValues()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input: currentTransaction.getInputs()) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.getUtxo().getValue() != tempOutput.getValue()) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for (TransactionOutput output: currentTransaction.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if (currentTransaction.getOutputs().get(0).getTo() != currentTransaction.getTo()) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.getOutputs().get(1).getTo() != currentTransaction.getFrom()) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }
}
