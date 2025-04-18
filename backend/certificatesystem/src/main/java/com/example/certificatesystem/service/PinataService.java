// PinataService.java
package com.example.certificatesystem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PinataService {
    
    private static final String PINATA_API_URL = "https://api.pinata.cloud/pinning/pinJSONToIPFS";
    private static final String API_KEY = "816e8ca2bffe4f19f4a0";
    private static final String API_SECRET = "51dcfe8abc5ff96f9e41fdb798e6f7cb74d503f8065d558a6e2fcae27650500f";
    private static final String JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySW5mb3JtYXRpb24iOnsiaWQiOiJjMDBkMGFhOS1kY2QwLTQ1NjAtYmVmNy04OTQwNTQ4OTEyNDYiLCJlbWFpbCI6InN1bWVkaDN0YXl3YWRlQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJwaW5fcG9saWN5Ijp7InJlZ2lvbnMiOlt7ImRlc2lyZWRSZXBsaWNhdGlvbkNvdW50IjoxLCJpZCI6IkZSQTEifSx7ImRlc2lyZWRSZXBsaWNhdGlvbkNvdW50IjoxLCJpZCI6Ik5ZQzEifV0sInZlcnNpb24iOjF9LCJtZmFfZW5hYmxlZCI6ZmFsc2UsInN0YXR1cyI6IkFDVElWRSJ9LCJhdXRoZW50aWNhdGlvblR5cGUiOiJzY29wZWRLZXkiLCJzY29wZWRLZXlLZXkiOiI4MTZlOGNhMmJmZmU0ZjE5ZjRhMCIsInNjb3BlZEtleVNlY3JldCI6IjUxZGNmZThhYmM1ZmY5NmY5ZTQxZmRiNzk4ZTZmN2NiNzRkNTAzZjgwNjVkNTU4YTZlMmZjYWUyNzY1MDUwMGYiLCJleHAiOjE3NzU1OTA2ODJ9.pTvCjtRAq51JRqFFqpdENIwL14QQ7lDIwI3Vz72vd_o";
    
    private final RestTemplate restTemplate;
    
    public PinataService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Stores certificate signature and hash on Pinata IPFS
     * 
     * @param certificateId The unique certificate ID
     * @param dataHash The certificate data hash in hex format
     * @param signature The signature in hex format
     * @return The IPFS hash (CID) if successful, otherwise null
     */
    public String storeCertificateSignature(String certificateId, String dataHash, String signature) {
        try {
            // Prepare headers with Pinata API credentials
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("pinata_api_key", API_KEY);
            headers.set("pinata_secret_api_key", API_SECRET);
            // Alternatively, use JWT auth
            // headers.set("Authorization", "Bearer " + JWT);
            
            // Create the metadata and content for Pinata
            Map<String, Object> pinataOptions = new HashMap<>();
            pinataOptions.put("cidVersion", 1);
            
            Map<String, Object> pinataMetadata = new HashMap<>();
            pinataMetadata.put("name", "Certificate-" + certificateId);
            
            Map<String, Object> certData = new HashMap<>();
            certData.put("certificateId", certificateId);
            certData.put("dataHash", dataHash);
            certData.put("signature", signature);
            certData.put("timestamp", System.currentTimeMillis());
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("pinataOptions", pinataOptions);
            requestBody.put("pinataMetadata", pinataMetadata);
            requestBody.put("pinataContent", certData);
            
            // Make the API call to Pinata
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                PINATA_API_URL,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String ipfsHash = (String) response.getBody().get("IpfsHash");
                log.info("Certificate signature stored on IPFS with hash: {}", ipfsHash);
                return ipfsHash;
            } else {
                log.error("Failed to store on IPFS. Response: {}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("Error storing certificate signature on IPFS", e);
            return null;
        }
    }
}