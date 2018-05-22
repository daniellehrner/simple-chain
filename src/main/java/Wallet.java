import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private HashMap<String,TransactionOutput> utxos = new HashMap<>();

    Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String getPrivateKeyString() {
        return StringUtil.getStringFromKey(privateKey);
    }

    String getPublicKeyString() {
        return StringUtil.getStringFromKey(publicKey);
    }

    PublicKey getPublicKey() {
        return publicKey;
    }

    PrivateKey getPrivateKey() {
        return privateKey;
    }

    float getBalance() {
        float total = 0;

        for (Map.Entry<String, TransactionOutput> item : SimpleChain.getUtxos().entrySet()) {
            TransactionOutput utxo = item.getValue();

            if (utxo.belongsToKey(publicKey)) {
                utxos.put(utxo.getId(), utxo);
                total += utxo.getValue();
            }
        }

        return total;
    }

    Transaction sendFunds(PublicKey to, float value) {
        if (getBalance() < value) {
            System.out.println("Not enought funds to send");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();
        float total = 0;

        for (Map.Entry<String, TransactionOutput> item : utxos.entrySet()) {
            TransactionOutput utxo = item.getValue();
            total += utxo.getValue();
            inputs.add(new TransactionInput(utxo.getId()));

            if (total > value) {
                break;
            }
        }

        Transaction transaction = new Transaction(publicKey, to, value, inputs);
        transaction.sign(privateKey);

        for (TransactionInput input : inputs) {
            utxos.remove(input.getTransactionOutputId());
        }

        return transaction;
    }
}
