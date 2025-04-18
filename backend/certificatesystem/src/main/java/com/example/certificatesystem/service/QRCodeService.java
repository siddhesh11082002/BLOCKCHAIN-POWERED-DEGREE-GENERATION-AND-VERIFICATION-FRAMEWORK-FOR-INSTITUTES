// QRCodeService.java (unchanged)
package com.example.certificatesystem.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    public String generateQRCodeBase64(Map<String, String> data) throws WriterException, IOException {
        // Convert data to JSON string
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!first) {
                builder.append(",");
            }
            builder.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        builder.append("}");
        String qrContent = builder.toString();
        
        byte[] qrBytes = generateQRCodeBytes(qrContent);
        return Base64.getEncoder().encodeToString(qrBytes);
    }
    
    public byte[] generateQRCodeBytes(String qrContent) throws WriterException, IOException {
        // Configure QR code parameters
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Higher error correction
        hints.put(EncodeHintType.MARGIN, 2); // Margin (quiet zone)
        
        // Generate QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300, hints);
        
        // Convert to image
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        // Convert to bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        return baos.toByteArray();
    }
}