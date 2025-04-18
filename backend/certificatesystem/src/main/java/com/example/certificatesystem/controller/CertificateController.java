package com.example.certificatesystem.controller;

import com.example.certificatesystem.model.CertificateRequest;
import com.example.certificatesystem.model.CertificateResponse;
import com.example.certificatesystem.model.VerificationResponse;
import com.example.certificatesystem.repository.StudentRepository;
import com.example.certificatesystem.service.CertificateService;
import com.example.certificatesystem.service.EmailService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")

@Slf4j
public class CertificateController {
    private final CertificateService certificateService;
    
    // Temporary storage for generated certificates (for prototype only)
    // In a real application, this would be stored in a database
    private final Map<String, byte[]> certificateStorage = new HashMap<>();
    
    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
        log.info("CertificateController initialized");
    }
    
    @Autowired
    private StudentRepository studentRepository;
    
    @PostMapping("/generate")
    public ResponseEntity<?> generateCertificate(@Valid @RequestBody CertificateRequest request) {
        log.info("Received certificate generation request for: {}", request.getStudentName());
        
        try {
            CertificateResponse response = certificateService.generateCertificate(request);
            
            // Store certificate for download (for prototype only)
            certificateStorage.put(response.getCertificateId(), response.getCertificatePdf());
            
            // Convert PDF to Base64 for response
            String pdfBase64 = Base64.getEncoder().encodeToString(response.getCertificatePdf());
            
            // Create response without binary PDF data for JSON serialization
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("success", response.isSuccess());
            jsonResponse.put("certificateId", response.getCertificateId());
            jsonResponse.put("transactionId", response.getTransactionId());
            jsonResponse.put("certificatePdfBase64", pdfBase64);
            
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            log.error("Error generating certificate", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to generate certificate: " + e.getMessage()));
        }
    }
    
 // Add this to CertificateController.java

    @PostMapping("/generateForStudent/{studentId}")
    public ResponseEntity<?> generateCertificateForStudent(@PathVariable Long studentId) {
        log.info("Received certificate generation request for student ID: {}", studentId);
        
        try {
            CertificateResponse response = certificateService.generateCertificateForStudent(studentId);
            
            // Store certificate for download (for prototype only)
            certificateStorage.put(response.getCertificateId(), response.getCertificatePdf());
            
            // Convert PDF to Base64 for response
            String pdfBase64 = Base64.getEncoder().encodeToString(response.getCertificatePdf());
            
            // Create response without binary PDF data for JSON serialization
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("success", response.isSuccess());
            jsonResponse.put("certificateId", response.getCertificateId());
            jsonResponse.put("transactionId", response.getTransactionId());
            jsonResponse.put("message", "Certificate successfully generated");
            
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            log.error("Error generating certificate for student", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to generate certificate: " + e.getMessage()));
        }
    }
    
    @PostMapping("/generate-form")
    public ResponseEntity<?> generateCertificateForm(
            @RequestParam("studentName") String studentName,
            @RequestParam("studentId") String studentId,
            @RequestParam("universityName") String universityName,
            @RequestParam("degreeName") String degreeName,
            @RequestParam("graduationDate") String graduationDate) {
        
        // Create the certificate request object
        CertificateRequest request = new CertificateRequest();
        request.setStudentName(studentName);
        request.setStudentId(studentId);
        request.setUniversityName(universityName);
        request.setDegreeName(degreeName);
        request.setGraduationDate(graduationDate);
        
        try {
            CertificateResponse response = certificateService.generateCertificate(request);
            
            // Create response without binary PDF data for JSON serialization
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("success", response.isSuccess());
            jsonResponse.put("certificateId", response.getCertificateId());
            jsonResponse.put("transactionId", response.getTransactionId());
            
            if (response.getCertificatePdf() != null) {
                String pdfBase64 = Base64.getEncoder().encodeToString(response.getCertificatePdf());
                jsonResponse.put("certificatePdfBase64", pdfBase64);
            }
            
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            log.error("Error generating certificate", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to generate certificate: " + e.getMessage()));
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCertificate(@RequestParam("pdf") MultipartFile pdfFile) {
        log.info("Received certificate verification request via PDF upload");
        
        if (pdfFile.isEmpty() || !pdfFile.getContentType().equals("application/pdf")) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Please upload a valid PDF file"));
        }
        
        try {
            VerificationResponse response = certificateService.verifyCertificate(pdfFile.getBytes());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error verifying certificate from PDF", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to verify certificate: " + e.getMessage()));
        }
    }
    
    @PostMapping("/verify/qr")
    public ResponseEntity<?> verifyFromQr(@RequestBody Map<String, String> request) {
        String qrData = request.get("qrData");
        log.info("Received certificate verification request via QR code");
        
        if (qrData == null || qrData.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "QR code data is required"));
        }
        
        try {
            VerificationResponse response = certificateService.verifyFromQrData(qrData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error verifying certificate from QR", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to verify certificate: " + e.getMessage()));
        }
    }
    
    @GetMapping("/download/{certificateId}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String certificateId) {
        log.info("Received certificate download request for ID: {}", certificateId);
        
        byte[] certificateBytes = certificateStorage.get(certificateId);
        
        if (certificateBytes == null) {
            log.warn("Certificate not found for ID: {}", certificateId);
            return ResponseEntity.notFound().build();
        }
        
        // Find and delete student with this certificate ID
        try {
            // You'll need to inject StudentRepository or StudentService
            studentRepository.findByCertificateId(certificateId).ifPresent(student -> {
                log.info("Deleting student with ID {} after certificate download", student.getId());
                studentRepository.delete(student);
            });
        } catch (Exception e) {
            // Just log the error but continue with download
            log.error("Error deleting student after certificate download: {}", e.getMessage());
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", certificateId + ".pdf");
        
        return new ResponseEntity<>(certificateBytes, headers, HttpStatus.OK);
    }
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/email/{certificateId}")
    public ResponseEntity<?> emailCertificate(
            @PathVariable String certificateId,
            @RequestBody Map<String, String> emailRequest) {
        
        String email = emailRequest.get("email");
        log.info("Received request to email certificate ID: {} to: {}", certificateId, email);
        
        if (email == null || email.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Email address is required"));
        }
        
        try {
            byte[] certificateBytes = certificateStorage.get(certificateId);
            
            if (certificateBytes == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Get student details for more personalized email
            String studentName = emailRequest.get("studentName");
            String degreeName = emailRequest.get("degreeName");
            String universityName = emailRequest.get("universityName");
            
            // Create HTML email content
            String emailContent = String.format(
                "<html><body>" +
                "<h2>Certificate from %s</h2>" +
                "<p>Dear %s,</p>" +
                "<p>Congratulations on your achievement! Please find attached your %s certificate.</p>" +
                "<p>This certificate has been securely verified using blockchain technology.</p>" +
                "<p>Regards,<br>%s Certificate Authority</p>" +
                "</body></html>",
                universityName != null ? universityName : "the University",
                studentName != null ? studentName : "Student",
                degreeName != null ? degreeName : "academic",
                universityName != null ? universityName : "University"
            );
            
            // Send email with certificate attachment
            emailService.sendMessageWithAttachment(
                email,
                "Your Academic Certificate",
                emailContent,
                "Certificate_" + certificateId + ".pdf",
                certificateBytes
            );
            
            log.info("Certificate {} successfully emailed to {}", certificateId, email);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Certificate successfully sent to " + email
            ));
        } catch (Exception e) {
            log.error("Error sending certificate email", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to send certificate: " + e.getMessage()));
        }
    }
    
}