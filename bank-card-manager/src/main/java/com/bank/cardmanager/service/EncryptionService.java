package com.bank.cardmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {
    
    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;
    
    public EncryptionService(@Value("${app.encryption.secret}") String secret) {
        // Ensure key is 16, 24, or 32 bytes
        byte[] key = secret.getBytes();
        if (key.length != 16 && key.length != 24 && key.length != 32) {
            throw new IllegalArgumentException("Encryption key must be 16, 24, or 32 bytes");
        }
        this.secretKey = new SecretKeySpec(key, ALGORITHM);
    }
    
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}