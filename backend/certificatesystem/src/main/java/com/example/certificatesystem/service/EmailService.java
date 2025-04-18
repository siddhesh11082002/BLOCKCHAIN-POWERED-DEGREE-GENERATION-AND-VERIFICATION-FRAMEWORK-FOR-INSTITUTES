// src/main/java/com/example/certificatesystem/service/EmailService.java
package com.example.certificatesystem.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
    void sendMessageWithAttachment(String to, String subject, String text, String attachmentName, byte[] attachment);
}