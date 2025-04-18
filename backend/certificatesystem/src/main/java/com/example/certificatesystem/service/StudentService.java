// StudentService.java
package com.example.certificatesystem.service;

import com.example.certificatesystem.model.Student;
import com.example.certificatesystem.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.print.DocFlavor.STRING;

@Service
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public List<Student> getStudentsByStatus(Student.CertificateStatus status) {
        return studentRepository.findByStatus(status);
    }

    @Transactional
    public Student createStudent(Student student) {
        if (studentRepository.existsByStudentId(student.getStudentId())) {
            throw new RuntimeException("Student ID already exists");
        }
        
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        return studentRepository.save(student);
    }

    @Transactional
    public boolean updateStudentStatus(Long id, Student.CertificateStatus status) {
        return studentRepository.findById(id).map(student -> {
            student.setStatus(status);
            studentRepository.save(student);
            return true;
        }).orElse(false);
    }

    @Transactional
    public List<Student> importStudentsFromExcel(MultipartFile file) throws IOException {
        List<Student> students = new ArrayList<>();
        Set<String> existingIds = new HashSet<>();
        Set<String> existingEmails = new HashSet<>();
        List<String> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // Extract header row to identify columns
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columns = new HashMap<>();
            
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    columns.put(cell.getStringCellValue().trim().toLowerCase(), i);
                }
            }
            
            // Validate required columns exist
            List<String> requiredColumns = Arrays.asList(
                "student name", "student id", "university name", 
                "degree name", "graduation date", "email"
            );
            
            for (String column : requiredColumns) {
                if (!columns.containsKey(column)) {
                    throw new IOException("Required column missing: " + column);
                }
            }
            
            // Collect existing student IDs and emails to check duplicates
            studentRepository.findAll().forEach(s -> {
                existingIds.add(s.getStudentId());
                existingEmails.add(s.getEmail());
            });
            
            // Process data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Student student = new Student();
                    
                    // Extract data from cells
                    student.setStudentName(getCellValueAsString(row.getCell(columns.get("student name"))));
                    student.setStudentId(getCellValueAsString(row.getCell(columns.get("student id"))));
                    student.setUniversityName(getCellValueAsString(row.getCell(columns.get("university name"))));
                    student.setDegreeName(getCellValueAsString(row.getCell(columns.get("degree name"))));
                    student.setGraduationDate(getCellValueAsString(row.getCell(columns.get("graduation date"))));
                    student.setEmail(getCellValueAsString(row.getCell(columns.get("email"))));
                    
                    // Set default status as PENDING
                    student.setStatus(Student.CertificateStatus.PENDING);
                    
                    // Check for duplicate IDs
                    if (existingIds.contains(student.getStudentId())) {
                        errors.add("Row " + (i+1) + ": Student ID already exists: " + student.getStudentId());
                        continue;
                    }
                    
                    // Check for duplicate emails
                    if (existingEmails.contains(student.getEmail())) {
                        errors.add("Row " + (i+1) + ": Email already exists: " + student.getEmail());
                        continue;
                    }
                    
                    // Add to processing list and update tracking sets
                    students.add(student);
                    existingIds.add(student.getStudentId());
                    existingEmails.add(student.getEmail());
                    
                } catch (Exception e) {
                    errors.add("Error processing row " + (i+1) + ": " + e.getMessage());
                }
            }
            
            // If there were any errors, throw exception with details
            if (!errors.isEmpty()) {
                throw new IOException("Errors found in Excel import: " + String.join("; ", errors));
            }
            
            // Save all valid students
            return studentRepository.saveAll(students);
        }
    }
    
    // Helper method to handle different cell types
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
    
    public StudentRepository.StatusCounts getStatusCounts() {
        return studentRepository.getStatusCounts();
    }
    
    public List<StudentRepository.DepartmentStats> getDepartmentStatistics() {
        return studentRepository.getDepartmentStatistics();
    }

    @Transactional
    public void updateCertificateInfo(Long studentId, String certificateId, String transactionId) {
        studentRepository.findById(studentId).ifPresent(student -> {
            student.setCertificateId(certificateId);
            student.setTransactionId(transactionId);
            student.setStatus(Student.CertificateStatus.ISSUED);
            studentRepository.save(student);
        });
    }
}