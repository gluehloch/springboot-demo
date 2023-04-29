package de.winkler.springboot.jwt;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * @author visruthcv
 *
 */
public class CryptoUtil {

    private static final String ALGORITHM = "RSA";

    private final KeyStoreService keyStoreService;

    public CryptoUtil(KeyStoreService keyStoreService) {
        this.keyStoreService = keyStoreService;
    }

    /**
     * Entschüsseln
     */
    public byte[] encrypt(byte[] inputData)
            throws Exception {

        PublicKey key = keyStoreService.publicKey().orElseThrow();

        /*
        PublicKey key = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(publicKey));
         */

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(inputData);

        return encryptedBytes;
    }

    /**
     * Verschlüsseln
     */
    public byte[] decrypt(byte[] inputData)
            throws Exception {

        Key key = keyStoreService.privateKey().orElseThrow();
        /*
        PrivateKey key = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(privateKey));
         */

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedBytes = cipher.doFinal(inputData);

        return decryptedBytes;
    }

    /*
    public static KeyPair generateKeyPair()
            throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

        // 512 is keysize
        keyGen.initialize(512, random);

        KeyPair generateKeyPair = keyGen.generateKeyPair();
        return generateKeyPair;
    }
    */

    /*
    public static void main(String[] args) throws Exception {
        KeyPair generateKeyPair = generateKeyPair();

        byte[] publicKey = generateKeyPair.getPublic().getEncoded();
        byte[] privateKey = generateKeyPair.getPrivate().getEncoded();

        byte[] encryptedData = encrypt(publicKey,
                "hi this is Visruth here".getBytes());

        byte[] decryptedData = decrypt(privateKey, encryptedData);

        System.out.println(new String(decryptedData));
    }
     */

}