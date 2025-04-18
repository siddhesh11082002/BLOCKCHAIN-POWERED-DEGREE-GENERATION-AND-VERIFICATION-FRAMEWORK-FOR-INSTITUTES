package com.example.certificatesystem.service;

import com.example.certificatesystem.model.CertificateRequest;
import com.example.certificatesystem.model.CertificateResponse;
import com.example.certificatesystem.model.VerificationResponse;
import com.example.certificatesystem.repository.StudentRepository;
import com.example.certificatesystem.model.Student;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

@Service
@Slf4j
public class CertificateService {
    private final CryptoService cryptoService;
    private final BlockchainService blockchainService;
    private final PdfService pdfService;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private PinataService pinataService;
    
    @Autowired
    public CertificateService(CryptoService cryptoService, 
                              BlockchainService blockchainService,
                              PdfService pdfService) {
        this.cryptoService = cryptoService;
        this.blockchainService = blockchainService;
        this.pdfService = pdfService;
        log.info("CertificateService initialized");
    }
    
    @Transactional
    public CertificateResponse generateCertificateForStudent(Long studentId) throws Exception {
        return studentRepository.findById(studentId).map(student -> {
            try {
                // Only process QUEUED students
                if (student.getStatus() != Student.CertificateStatus.QUEUED) {
                    throw new RuntimeException("Student is not in QUEUED status");
                }
                
                // Create certificate request from student data
                CertificateRequest request = new CertificateRequest();
                request.setStudentName(student.getStudentName());
                request.setStudentId(student.getStudentId());
                request.setUniversityName(student.getUniversityName());
                request.setDegreeName(student.getDegreeName());
                request.setGraduationDate(student.getGraduationDate());
                
                // Generate certificate
                CertificateResponse response = generateCertificate(request);
                
                // Update student record with certificate info
                student.setCertificateId(response.getCertificateId());
                student.setTransactionId(response.getTransactionId());
                student.setStatus(Student.CertificateStatus.ISSUED);
                studentRepository.save(student);
                
                return response;
            } catch (Exception e) {
                log.error("Error generating certificate for student ID {}: {}", studentId, e.getMessage());
                throw new RuntimeException("Failed to generate certificate: " + e.getMessage());
            }
        }).orElseThrow(() -> new RuntimeException("Student not found"));
    }
    
    
    public CertificateResponse generateCertificate(CertificateRequest request) throws Exception {
        log.info("Generating certificate for student: {}, degree: {}", 
                 request.getStudentName(), request.getDegreeName());
        
        try {
            // Generate certificate ID if not provided
            if (request.getCertificateId() == null || request.getCertificateId().isEmpty()) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                request.setCertificateId("CERT-" + timestamp);
                log.info("Generated certificate ID: {}", request.getCertificateId());
            }
            
            // Create data string from certificate fields
            String certificateData = createCertificateDataString(request);
            
            // Hash the data
            byte[] dataHash = cryptoService.hashData(certificateData);
            String dataHashHex = cryptoService.bytesToHex(dataHash);
            log.debug("Certificate data hash: {}", dataHashHex);
            
            // Sign the hash
            byte[] signature = cryptoService.sign(dataHash);
            
            
            // Store on Pinata IPFS (add this line)
            String signatureHex = cryptoService.bytesToHex(signature);
            String ipfsHash = pinataService.storeCertificateSignature(request.getCertificateId(), dataHashHex, signatureHex);
            log.info("Certificate signature stored on IPFS with hash: {}", ipfsHash);
            
            // Store on blockchain
            String transactionId = blockchainService.storeCertificate(dataHash, signature);
            log.info("Certificate signature stored on blockchain, tx: {}", transactionId);
            
            // Generate PDF
            byte[] pdfBytes = pdfService.generateCertificatePdf(request, dataHashHex, transactionId, ipfsHash);
            
            // Create response
            CertificateResponse response = new CertificateResponse();
            response.setSuccess(true);
            response.setCertificateId(request.getCertificateId());
            response.setTransactionId(transactionId);
            response.setCertificatePdf(pdfBytes);
            
            return response;
        } catch (Exception e) {
            log.error("Error generating certificate", e);
            throw e;
        }
    }
    
    private String createCertificateDataString(CertificateRequest request) {
    	// Concatenate fields in a fixed order to ensure hash consistency
        // Excluding studentId as it's not included in the PDF for verification
        return request.getCertificateId() + 
               request.getStudentName() + 
               request.getUniversityName() + 
               request.getDegreeName() + 
               request.getGraduationDate();
    }
    
    public VerificationResponse verifyCertificate(byte[] pdfBytes) throws Exception {
        log.info("Verifying certificate from PDF upload");
        
        try {
            // Extract fields from PDF
            Map<String, String> fields = pdfService.extractFieldsFromPdf(pdfBytes);
            
            if (fields.isEmpty() || !fields.containsKey("certificateId")) {
                log.warn("Could not extract required fields from PDF");
                VerificationResponse response = new VerificationResponse();
                response.setValid(false);
                return response;
            }
            
            // Reconstruct data string - matching the generation format
            // Excluding studentId as it's not available in the PDF
            String certificateData = fields.get("certificateId") + 
                                    fields.get("studentName") + 
                                    fields.get("universityName") + 
                                    fields.get("degreeName") + 
                                    fields.get("graduationDate");
            
            // Hash the data
            byte[] dataHash = cryptoService.hashData(certificateData);
            
            // Get signature from blockchain
            byte[] storedSignature = blockchainService.getSignature(dataHash);
            
            if (storedSignature.length == 0) {
                log.warn("No signature found on blockchain for certificate");
                VerificationResponse response = new VerificationResponse();
                response.setValid(false);
                response.setCertificateDetails(fields);
                return response;
            }
            
            // Verify signature
            boolean isValid = cryptoService.verify(dataHash, storedSignature);
            log.info("Certificate verification result: {}", isValid);
            
            // Create response
            VerificationResponse response = new VerificationResponse();
            response.setValid(isValid);
            response.setCertificateDetails(fields);
            
            return response;
        } catch (Exception e) {
            log.error("Error verifying certificate from PDF", e);
            throw e;
        }
    }
    
    public VerificationResponse verifyFromQrData(String qrData) throws Exception {
        log.info("Verifying certificate from QR code data");
        
        try {
            // Parse JSON from QR code
            JSONObject json = new JSONObject(qrData);
            
            // Extract fields
            Map<String, String> fields = new HashMap<>();
            fields.put("certificateId", json.getString("certificateId"));
            fields.put("studentName", json.getString("studentName"));
            fields.put("studentId", json.getString("studentId")); // Still store this for display purposes
            fields.put("universityName", json.getString("universityName"));
            fields.put("degreeName", json.getString("degreeName"));
            fields.put("graduationDate", json.getString("graduationDate"));
            fields.put("transactionId", json.getString("transactionId"));
            
            // Get data hash from QR
            byte[] dataHash = cryptoService.hexToBytes(json.getString("dataHash"));
            
            // Get signature from blockchain
            byte[] storedSignature = blockchainService.getSignature(dataHash);
            
            if (storedSignature.length == 0) {
                log.warn("No signature found on blockchain for certificate");
                VerificationResponse response = new VerificationResponse();
                response.setValid(false);
                response.setCertificateDetails(fields);
                return response;
            }
            
            // Verify signature
            boolean isValid = cryptoService.verify(dataHash, storedSignature);
            log.info("Certificate verification result from QR: {}", isValid);
            
            // Create response
            VerificationResponse response = new VerificationResponse();
            response.setValid(isValid);
            response.setCertificateDetails(fields);
            
            return response;
        } catch (Exception e) {
            log.error("Error verifying certificate from QR data", e);
            throw e;
        }
    }
}