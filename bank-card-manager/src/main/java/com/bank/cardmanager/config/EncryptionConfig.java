package com.bank.cardmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class EncryptionConfig {
    
    @Value("${app.encryption.secret}")
    private String encryptionSecret;
    
    @Bean
    public SecretKeySpec encryptionKey() {
        // Ensure the key is exactly 32 bytes for AES-256
        byte[] keyBytes = encryptionSecret.getBytes(StandardCharsets.UTF_8);
        byte[] aesKey = new byte[32];
        System.arraycopy(keyBytes, 0, aesKey, 0, Math.min(keyBytes.length, aesKey.length));
        
        // If the key is shorter than 32 bytes, pad with zeros (not recommended for production)
        if (keyBytes.length < 32) {
            for (int i = keyBytes.length; i < 32; i++) {
                aesKey[i] = 0;
            }
        }
        
        return new SecretKeySpec(aesKey, "AES");
    }
    
    @Bean
    public Base64.Encoder base64Encoder() {
        return Base64.getEncoder();
    }
    
    @Bean
    public Base64.Decoder base64Decoder() {
        return Base64.getDecoder();
    }
}