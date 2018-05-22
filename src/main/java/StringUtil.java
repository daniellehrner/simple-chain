import java.security.Key;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;

class StringUtil {
    static String computeSha256Hash(String input) {
        try {
            MessageDigest digestInstance = MessageDigest.getInstance("SHA-256");

            byte[] hash = digestInstance.digest(input.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();

            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(computeSha256Hash(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }
}
