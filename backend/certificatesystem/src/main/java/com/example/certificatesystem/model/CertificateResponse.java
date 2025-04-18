package com.example.certificatesystem.model;

import lombok.Data;

@Data
public class CertificateResponse {
    private boolean success;
    private String certificateId;
    private String transactionId;
    private byte[] certificatePdf;
    
    // Useful for JSON serialization - don't include PDF bytes in toString
    @Override
    public String toString() {
        return "CertificateResponse{" +
                "success=" + success +
                ", certificateId='" + certificateId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", certificatePdf=[BINARY DATA]" +
                '}';
    }
}