package com.example.certificatesystem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.Signature;

@Service
@Slf4j
public class CryptoService {
    private final KeyPair keyPair;
    
    @Autowired
    public CryptoService(KeyPair keyPair) {
        this.keyPair = keyPair;
        log.info("CryptoService initialized with RSA key pair");
    }
    
    public byte[] hashData(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        log.debug("Generated hash for data: {}", bytesToHex(hash));
        return hash;
    }
    
    public byte[] sign(byte[] dataHash) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(dataHash);
        byte[] signatureBytes = signature.sign();
        log.debug("Signed hash with private key, signature: {}", bytesToHex(signatureBytes));
        return signatureBytes;
    }
    
    public boolean verify(byte[] dataHash, byte[] signatureBytes) throws Exception {
        if (signatureBytes == null || signatureBytes.length == 0) {
            log.warn("Empty signature provided for verification");
            return false;
        }
        
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(keyPair.getPublic());
        signature.update(dataHash);
        boolean isValid = signature.verify(signatureBytes);
        log.debug("Signature verification result: {}", isValid);
        return isValid;
    }
    
    public String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    public byte[] hexToBytes(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }
        
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                               + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}
