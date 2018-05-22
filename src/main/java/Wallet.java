import java.security.*;
import java.security.spec.ECGenParameterSpec;

class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

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
}
