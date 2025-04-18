package com.example.certificatesystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CryptoConfig {
    
    @Value("${crypto.key.private:#{null}}")
    private String privateKeyBase64;
    
    @Value("${crypto.key.public:#{null}}")
    private String publicKeyBase64;
    
    @Bean
    public KeyPair rsaKeyPair() throws Exception {
        // If keys are defined in properties, use them
        if (privateKeyBase64 != null && publicKeyBase64 != null && 
            !privateKeyBase64.isEmpty() && !publicKeyBase64.isEmpty()) {
            
            log.info("Loading RSA key pair from application properties");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            
            try {
                // Decode the public key
                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
                
                // Try to decode the private key (may be in PKCS8 format)
                try {
                    byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
                    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                    PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
                    log.info("RSA key pair loaded successfully from properties");
                    return new KeyPair(publicKey, privateKey);
                } catch (Exception e) {
                    log.warn("Failed to load private key with PKCS8 format: {}", e.getMessage());
                    log.warn("Your private key appears to be in PKCS1 format which requires conversion.");
                    log.warn("For production use, please convert your key to PKCS8 format.");
                    
                    // For demo purposes, we'll generate a new key pair
                    log.info("Generating a temporary key pair instead");
                    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                    generator.initialize(2048);
                    return generator.generateKeyPair();
                }
            } catch (Exception e) {
                log.error("Failed to load keys from properties: {}", e.getMessage());
                throw e;
            }
        }
        
        // If keys are not defined, generate a new pair and print them
        log.info("No RSA keys found in properties, generating new key pair");
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        
        // Output the encoded keys so they can be added to properties
        String encodedPrivate = Base64.getEncoder().encodeToString(
            keyPair.getPrivate().getEncoded());
        String encodedPublic = Base64.getEncoder().encodeToString(
            keyPair.getPublic().getEncoded());
        
        log.info("Generated new RSA key pair. Add these to your application.properties:");
        log.info("crypto.key.private=" + encodedPrivate);
        log.info("crypto.key.public=" + encodedPublic);
        
        return keyPair;
    }
}