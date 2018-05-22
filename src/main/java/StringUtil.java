import java.security.Key;
import java.security.MessageDigest;
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
}
