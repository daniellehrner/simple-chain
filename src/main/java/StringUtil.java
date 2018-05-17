import java.security.MessageDigest;

public class StringUtil {
    public static String computeSha256Hash(String input) {
        try {
            MessageDigest digestInstance = MessageDigest.getInstance("SHA-256");

            byte[] hash = digestInstance.digest(input.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; ++i) {
                String hex = Integer.toHexString(0xff & hash[i]);

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
}
