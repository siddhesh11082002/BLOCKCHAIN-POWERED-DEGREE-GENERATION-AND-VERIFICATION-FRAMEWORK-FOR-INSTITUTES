// Updated PdfService.java with proper landscape orientation
package com.example.certificatesystem.service;

import com.example.certificatesystem.model.CertificateRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfService {
    
    private final QRCodeService qrCodeService;
    
    public PdfService(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }
    
    public byte[] generateCertificatePdf(CertificateRequest request, String dataHash, String transactionId, String ipfsHash) throws Exception {
        try (PDDocument document = new PDDocument()) {
            // Create a landscape page explicitly
            PDRectangle pageSize = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
            PDPage page = new PDPage(pageSize);
            document.addPage(page);
            
            // Create content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            // Get page dimensions (now in landscape)
            float pageWidth = pageSize.getWidth();
            float pageHeight = pageSize.getHeight();
            float margin = 50;
            
            // Create QR code data
            // Include all fields for display, but the dataHash is calculated without studentId
            Map<String, String> qrData = new HashMap<>();
            qrData.put("certificateId", request.getCertificateId());
            qrData.put("studentName", request.getStudentName());
            qrData.put("studentId", request.getStudentId()); // Keep this for display but not for hash
            qrData.put("universityName", request.getUniversityName());
            qrData.put("degreeName", request.getDegreeName());
            qrData.put("graduationDate", request.getGraduationDate());
            qrData.put("dataHash", dataHash);
            qrData.put("transactionId", transactionId);
            
            // Create JSON from QR data
            JSONObject jsonObject = new JSONObject(qrData);
            String qrContent = jsonObject.toString();
            
            // Generate QR code
            byte[] qrCodeBytes = qrCodeService.generateQRCodeBytes(qrContent);
            PDImageXObject qrCode = PDImageXObject.createFromByteArray(document, qrCodeBytes, "QR Code");
            
            // Fonts
            PDFont titleFont = PDType1Font.TIMES_BOLD;
            PDFont subtitleFont = PDType1Font.TIMES_ROMAN;
            PDFont normalFont = PDType1Font.TIMES_ROMAN;
            PDFont italicFont = PDType1Font.TIMES_ITALIC;
            PDFont boldItalicFont = PDType1Font.TIMES_BOLD_ITALIC;
            
            // Colors
            Color navyBlue = new Color(31, 56, 100);
            Color darkGray = new Color(68, 68, 68);
            Color lightGray = new Color(200, 200, 200);
            
            // Draw border
            contentStream.setStrokingColor(navyBlue);
            contentStream.setLineWidth(2);
            contentStream.addRect(margin, margin, pageWidth - 2 * margin, pageHeight - 2 * margin);
            contentStream.stroke();
            
            // Inner border
            contentStream.setLineWidth(1);
            contentStream.addRect(margin + 10, margin + 10, pageWidth - 2 * (margin + 10), pageHeight - 2 * (margin + 10));
            contentStream.stroke();
            
            // Start adding text
            contentStream.beginText();
            
            // University name
            contentStream.setFont(titleFont, 28);
            contentStream.setNonStrokingColor(navyBlue);
            String universityText = request.getUniversityName();
            float titleWidth = titleFont.getStringWidth(universityText) / 1000 * 28;
            contentStream.newLineAtOffset((pageWidth - titleWidth) / 2, pageHeight - margin - 80);
            contentStream.showText(universityText);
            contentStream.endText();
            
            // Certificate title
            contentStream.beginText();
            contentStream.setFont(subtitleFont, 22);
            contentStream.setNonStrokingColor(darkGray);
            String certTitle = "Certificate of Completion";
            float certTitleWidth = subtitleFont.getStringWidth(certTitle) / 1000 * 22;
            contentStream.newLineAtOffset((pageWidth - certTitleWidth) / 2, pageHeight - margin - 120);
            contentStream.showText(certTitle);
            contentStream.endText();
            
            // Horizontal line
            contentStream.setStrokingColor(lightGray);
            contentStream.setLineWidth(1);
            contentStream.moveTo(margin + 40, pageHeight - margin - 150);
            contentStream.lineTo(pageWidth - margin - 40, pageHeight - margin - 150);
            contentStream.stroke();
            
            // "This certifies that"
            contentStream.beginText();
            contentStream.setFont(normalFont, 16);
            contentStream.setNonStrokingColor(darkGray);
            String certifiesText = "This certifies that";
            float certifiesWidth = normalFont.getStringWidth(certifiesText) / 1000 * 16;
            contentStream.newLineAtOffset((pageWidth - certifiesWidth) / 2, pageHeight - margin - 200);
            contentStream.showText(certifiesText);
            contentStream.endText();
            
            // Student name
            contentStream.beginText();
            contentStream.setFont(boldItalicFont, 24);
            contentStream.setNonStrokingColor(navyBlue);
            String studentName = request.getStudentName();
            float nameWidth = boldItalicFont.getStringWidth(studentName) / 1000 * 24;
            contentStream.newLineAtOffset((pageWidth - nameWidth) / 2, pageHeight - margin - 240);
            contentStream.showText(studentName);
            contentStream.endText();
            
            // "has successfully completed the requirements for"
            contentStream.beginText();
            contentStream.setFont(normalFont, 16);
            contentStream.setNonStrokingColor(darkGray);
            String completedText = "has successfully completed the requirements for";
            float completedWidth = normalFont.getStringWidth(completedText) / 1000 * 16;
            contentStream.newLineAtOffset((pageWidth - completedWidth) / 2, pageHeight - margin - 280);
            contentStream.showText(completedText);
            contentStream.endText();
            
            // Degree name
            contentStream.beginText();
            contentStream.setFont(boldItalicFont, 20);
            contentStream.setNonStrokingColor(navyBlue);
            String degreeName = request.getDegreeName();
            float degreeWidth = boldItalicFont.getStringWidth(degreeName) / 1000 * 20;
            contentStream.newLineAtOffset((pageWidth - degreeWidth) / 2, pageHeight - margin - 320);
            contentStream.showText(degreeName);
            contentStream.endText();
            
            // Award date
            contentStream.beginText();
            contentStream.setFont(normalFont, 16);
            contentStream.setNonStrokingColor(darkGray);
            String formattedDate = formatGraduationDate(request.getGraduationDate());
            String awardDate = "Awarded on: " + formattedDate;
            float dateWidth = normalFont.getStringWidth(awardDate) / 1000 * 16;
            contentStream.newLineAtOffset((pageWidth - dateWidth) / 2, pageHeight - margin - 360);
            contentStream.showText(awardDate);
            contentStream.endText();
            
            // Signature lines
            float signatureY = pageHeight - margin - 440;
            float signatureWidth = 200;
            float spacing = 100;
            
            // Left signature (University President)
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.setLineWidth(1);
            contentStream.moveTo((pageWidth - signatureWidth - spacing) / 2, signatureY);
            contentStream.lineTo((pageWidth - spacing) / 2, signatureY);
            contentStream.stroke();
            
            contentStream.beginText();
            contentStream.setFont(normalFont, 16);
            contentStream.setNonStrokingColor(darkGray);
            String presidentText = "University President";
            float presidentWidth = normalFont.getStringWidth(presidentText) / 1000 * 16;
            contentStream.newLineAtOffset((pageWidth - signatureWidth - spacing) / 2 + (signatureWidth - presidentWidth) / 2, signatureY - 25);
            contentStream.showText(presidentText);
            contentStream.endText();
            
            // Right signature (Dean of Faculty)
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.moveTo((pageWidth + spacing) / 2, signatureY);
            contentStream.lineTo((pageWidth + signatureWidth + spacing) / 2, signatureY);
            contentStream.stroke();
            
            contentStream.beginText();
            contentStream.setFont(normalFont, 16);
            contentStream.setNonStrokingColor(darkGray);
            String deanText = "Dean of Faculty";
            float deanWidth = normalFont.getStringWidth(deanText) / 1000 * 16;
            contentStream.newLineAtOffset((pageWidth + spacing) / 2 + (signatureWidth - deanWidth) / 2, signatureY - 25);
            contentStream.showText(deanText);
            contentStream.endText();
            
            // Add QR code
            float qrSize = 80;
            contentStream.drawImage(qrCode, pageWidth - margin - qrSize - 10, margin + 30, qrSize, qrSize);
         // Also add a small text field for the IPFS CID near the certificate ID
            contentStream.beginText();
            contentStream.setFont(normalFont, 8);
            contentStream.setNonStrokingColor(lightGray);
            String ipfsText = "IPFS: " + ipfsHash;
            float ipfsTextWidth = normalFont.getStringWidth(ipfsText) / 1000 * 8;
            contentStream.newLineAtOffset(pageWidth - margin - ipfsTextWidth - 10, margin + 10);
            contentStream.showText(ipfsText);
            contentStream.endText();
            
            // Add certificate ID
            contentStream.beginText();
            contentStream.setFont(normalFont, 8);
            contentStream.setNonStrokingColor(lightGray);
            String certIdText = "Certificate ID: " + request.getCertificateId();
            float certIdWidth = normalFont.getStringWidth(certIdText) / 1000 * 8;
            contentStream.newLineAtOffset(pageWidth - margin - certIdWidth - 10, margin + 20);
            contentStream.showText(certIdText);
            contentStream.endText();
            
         
            contentStream.close();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new Exception("Failed to generate certificate PDF: " + e.getMessage(), e);
        }
    }
    
    private void extractIpfsHash(String text, Map<String, String> fields) {
        int ipfsIndex = text.indexOf("IPFS: ");
        if (ipfsIndex >= 0) {
            String ipfsLine = text.substring(ipfsIndex + "IPFS: ".length());
            String[] parts = ipfsLine.split("\\s");
            if (parts.length > 0) {
                fields.put("ipfsHash", parts[0].trim());
            }
        }
    }

    
    private String formatGraduationDate(String dateStr) {
        try {
            // Try to parse as ISO date (yyyy-MM-dd)
            LocalDate date = LocalDate.parse(dateStr);
            return date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        } catch (Exception e) {
            // If parsing fails, return as is
            return dateStr;
        }
    }
    
    public Map<String, String> extractFieldsFromPdf(byte[] pdfBytes) throws IOException {
        Map<String, String> fields = new HashMap<>();
        
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            // Since we know our own certificate format, we can extract text from specific positions
            // or look for specific patterns based on our PDF layout
            
            // Extract text from the PDF
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            // Extract fields based on known patterns in our certificate layout
            extractCertificateId(text, fields);
            extractStudentName(text, fields);
            extractUniversityName(text, fields);
            extractDegreeName(text, fields);
            extractGraduationDate(text, fields);
            extractIpfsHash(text, fields);
            
            // For debugging
            System.out.println("Extracted fields from PDF: " + fields);
        } catch (Exception e) {
            throw new IOException("Failed to extract fields from PDF: " + e.getMessage(), e);
        }
        
        return fields;
    }
    
    private void extractCertificateId(String text, Map<String, String> fields) {
        // Look for the certificate ID pattern
        int certIdIndex = text.indexOf("Certificate ID: ");
        if (certIdIndex >= 0) {
            String certIdLine = text.substring(certIdIndex);
            String[] parts = certIdLine.split("\\s+", 3);
            if (parts.length >= 3) {
                fields.put("certificateId", parts[2].trim());
            }
        }
    }
    
    private void extractStudentName(String text, Map<String, String> fields) {
        // Find student name which appears after "This certifies that"
        int certifiesIndex = text.indexOf("This certifies that");
        if (certifiesIndex >= 0) {
            String afterCertifies = text.substring(certifiesIndex + "This certifies that".length()).trim();
            String[] lines = afterCertifies.split("\\n");
            if (lines.length > 0) {
                // The student name should be the next non-empty line
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        fields.put("studentName", line.trim());
                        break;
                    }
                }
            }
        }
    }
    
    private void extractUniversityName(String text, Map<String, String> fields) {
        // University name is typically at the top of the certificate
        String[] lines = text.split("\\n");
        if (lines.length > 0) {
            for (int i = 0; i < Math.min(3, lines.length); i++) {
                if (!lines[i].trim().isEmpty() && 
                    !lines[i].contains("Certificate of") && 
                    !lines[i].contains("Certificate ID:")) {
                    fields.put("universityName", lines[i].trim());
                    break;
                }
            }
        }
    }
    
    private void extractDegreeName(String text, Map<String, String> fields) {
        // Find degree name which appears after "requirements for"
        int requirementsIndex = text.indexOf("requirements for");
        if (requirementsIndex >= 0) {
            String afterRequirements = text.substring(requirementsIndex + "requirements for".length()).trim();
            String[] lines = afterRequirements.split("\\n");
            if (lines.length > 0) {
                // The degree name should be the next non-empty line
                for (String line : lines) {
                    if (!line.trim().isEmpty() && !line.contains("Awarded on:")) {
                        fields.put("degreeName", line.trim());
                        break;
                    }
                }
            }
        }
    }
    
    private void extractGraduationDate(String text, Map<String, String> fields) {
        // Find graduation date which appears after "Awarded on:"
        int awardedIndex = text.indexOf("Awarded on:");
        if (awardedIndex >= 0) {
            String afterAwarded = text.substring(awardedIndex + "Awarded on:".length()).trim();
            String[] lines = afterAwarded.split("\\n");
            if (lines.length > 0) {
                // The date should be on the same line or next
                String datePart = lines[0].trim();
                fields.put("graduationDate", datePart);
            }
        }
    }
}