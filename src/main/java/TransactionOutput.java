import java.security.PublicKey;

class TransactionOutput {
    private String id;
    private PublicKey to;
    private float value;
    private String parentTransactionId;

    public TransactionOutput(PublicKey to, float value, String parentTransactionId) {
        this.to = to;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.computeSha256Hash(
            StringUtil.getStringFromKey(to) +
            Float.toString(value) +
            parentTransactionId
        );
    }

    public boolean belongsToKey(PublicKey key) {
        return key == to;
    }

    String getId() {
        return id;
    }

    float getValue() {
        return value;
    }

    PublicKey getTo() {
        return to;
    }
}
