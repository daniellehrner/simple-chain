import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;

class Transaction {
    private String transactionId;
    private final PublicKey from;
    private final PublicKey to;
    private final float value;
    private byte[] signature;

    private ArrayList<TransactionInput> inputs;
    private ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.inputs = inputs;
    }

    String getTransactionId() {
        return transactionId;
    }

    void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    PublicKey getTo() {
        return to;
    }

    PublicKey getFrom() {
        return from;
    }

    float getValue() {
        return value;
    }

    ArrayList<TransactionOutput> getOutputs() {
        return outputs;
    }

    ArrayList<TransactionInput> getInputs() {
        return inputs;
    }

    void addTransactionOutput(TransactionOutput transactionOutput) {
        outputs.add(transactionOutput);
    }

    public boolean processTransaction() {
        if (!verify()) {
            System.out.println("Transaction signature failed");
            return false;
        }

        for (TransactionInput input : inputs) {
            input.setUtxo(SimpleChain.getUtxos().get(input.getTransactionOutputId()));
        }

        if (getInputsValue() < SimpleChain.getMinimumTransaction()) {
            System.out.println("Transaction inputs are smaller than the minimum: " + getInputsValue());
            return false;
        }

        float leftOver = getInputsValue() - value;
        transactionId = computeHash();
        outputs.add(new TransactionOutput(this.to, value, transactionId));
        outputs.add(new TransactionOutput(this.from, leftOver, transactionId));

        for (TransactionOutput output : outputs) {
            SimpleChain.getUtxos().put(output.getId(), output);
        }

        for (TransactionInput input : inputs) {
            if (input.getUtxo() == null) {
                continue;
            }

            SimpleChain.getUtxos().remove(input.getUtxo().getId());
        }

        return true;
    }

    float getInputsValue() {
        float total = 0;

        for (TransactionInput input : inputs) {
            if (input.getUtxo() == null) {
                continue;
            }
            total += input.getUtxo().getValue();
        }

        return total;
    }

    float getOutputValues() {
        float total = 0;

        for (TransactionOutput output : outputs) {
            total += output.getValue();
        }

        return total;
    }

   void sign(PrivateKey privateKey) {
        signature = computeECDSASig(privateKey, getTransactionData());
    }

    boolean verify() {
        return verifyECDSASig(from, getTransactionData(), signature);
    }

    private String getTransactionData() {
        return StringUtil.getStringFromKey(from) + StringUtil.getStringFromKey(to) + Float.toString(value);
    }

    private String computeHash() {
        return StringUtil.computeSha256Hash(getTransactionData() + ++sequence);
    }

    private boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
         boolean result;

        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            result = ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private byte[] computeECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output;

        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] stringByte = input.getBytes();
            dsa.update(stringByte);
            output = dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return output;
    }
}
