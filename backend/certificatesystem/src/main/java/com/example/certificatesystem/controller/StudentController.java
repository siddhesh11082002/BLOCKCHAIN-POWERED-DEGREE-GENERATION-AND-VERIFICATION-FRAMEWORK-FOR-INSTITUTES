// StudentController.java
package com.example.certificatesystem.controller;

import com.example.certificatesystem.model.Student;
import com.example.certificatesystem.repository.StudentRepository;
import com.example.certificatesystem.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@Slf4j
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Student>> getStudentsByStatus(@PathVariable String status) {
        try {
            Student.CertificateStatus certificateStatus = Student.CertificateStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(studentService.getStudentsByStatus(certificateStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody Student student) {
        try {
            Student createdStudent = studentService.createStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStudentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        
        String status = statusUpdate.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
        }
        
        try {
            Student.CertificateStatus certificateStatus = Student.CertificateStatus.valueOf(status.toUpperCase());
            boolean updated = studentService.updateStudentStatus(id, certificateStatus);
            
            if (updated) {
                return ResponseEntity.ok(Map.of("message", "Status updated successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status value"));
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> importStudents(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please upload a file"));
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !(
                contentType.equals("application/vnd.ms-excel") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please upload an Excel file"));
        }
        
        try {
            List<Student> importedStudents = studentService.importStudentsFromExcel(file);
            return ResponseEntity.ok(Map.of(
                "message", "Successfully imported " + importedStudents.size() + " students",
                "students", importedStudents
            ));
        } catch (Exception e) {
            log.error("Error importing students from Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Get status counts
        StudentRepository.StatusCounts statusCounts = studentService.getStatusCounts();
        statistics.put("statusCounts", statusCounts);
        
        // Get department statistics
        List<StudentRepository.DepartmentStats> departmentStats = studentService.getDepartmentStatistics();
        statistics.put("departmentStats", departmentStats);
        
        return ResponseEntity.ok(statistics);
    }
}