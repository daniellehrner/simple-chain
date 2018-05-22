class TransactionInput {
    private String transactionOutputId;
    private TransactionOutput utxo;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    String getTransactionOutputId() {
        return transactionOutputId;
    }

    TransactionOutput getUtxo() {
        return utxo;
    }

    void setUtxo(TransactionOutput utxo) {
        this.utxo = utxo;
    }
}
