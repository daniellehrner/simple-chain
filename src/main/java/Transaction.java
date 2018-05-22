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

    private ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.inputs = inputs;
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
