package com.java.service.encdec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import com.java.service.encdec.exception.EncryptionException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Encryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static String encryption(String fileName, String data) throws Exception {
        // Simulate encryption (here, encoding with Base64 for simplicity)
        String publicKeyPath = fileName;

        // Load the public key
        PublicKey publicKey = loadPublicKey(publicKeyPath);

        // Generate AES key
        SecretKey aesKey = generateAESKey();

        // Encrypt data with AES key
        byte[] encryptedData = encryptDataWithAES(data, aesKey);

        // Encrypt AES key with public key
        byte[] encryptedAESKey = encryptAESKeyWithPublicKey(aesKey, publicKey);

        System.out.println("Encrypted Data: " + Base64.getEncoder().encodeToString(encryptedData));
        System.out.println("Encrypted AES Key: " + Base64.getEncoder().encodeToString(encryptedAESKey));

        return "Encrypted Data: " + Base64.getEncoder().encodeToString(encryptedData) + "\nEncrypted AES Key: "
                + Base64.getEncoder().encodeToString(encryptedAESKey);
    }

    private static PublicKey loadPublicKey(String publicKeyPath) throws Exception {
        try {
            String key = new String(Files.readAllBytes(Paths.get(publicKeyPath)))
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new EncryptionException("Error loading public key", e);
        }
    }

    private static SecretKey generateAESKey() throws Exception {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // AES-256
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new EncryptionException("Error generating AES key", e);
        }
    }

    private static byte[] encryptDataWithAES(String data, SecretKey aesKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new EncryptionException("Error encrypting data with AES", e);
        }
    }

    private static byte[] encryptAESKeyWithPublicKey(SecretKey aesKey, PublicKey publicKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(aesKey.getEncoded());
        } catch (Exception e) {
            throw new EncryptionException("Error encrypting AES key with public key", e);
        }
    }
}