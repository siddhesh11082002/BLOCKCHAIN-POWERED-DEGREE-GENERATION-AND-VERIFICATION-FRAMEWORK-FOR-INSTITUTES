package com.example.certificatesystem.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CertificateRequest {
    private String certificateId;
    
    @NotBlank(message = "Student name is required")
    private String studentName;
    
    @NotBlank(message = "Student ID is required")
    private String studentId;
    
    @NotBlank(message = "University name is required")
    private String universityName;
    
    @NotBlank(message = "Degree name is required")
    private String degreeName;
    
    @NotBlank(message = "Graduation date is required")
    private String graduationDate;
}
