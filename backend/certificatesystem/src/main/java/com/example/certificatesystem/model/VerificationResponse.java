package com.example.certificatesystem.model;

import lombok.Data;
import java.util.Map;

@Data
public class VerificationResponse {
    private boolean valid;
    private Map<String, String> certificateDetails;
}