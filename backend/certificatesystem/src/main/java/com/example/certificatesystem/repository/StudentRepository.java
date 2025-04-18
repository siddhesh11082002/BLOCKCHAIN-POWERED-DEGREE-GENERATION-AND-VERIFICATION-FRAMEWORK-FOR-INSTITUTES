// StudentRepository.java
package com.example.certificatesystem.repository;

import com.example.certificatesystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByCertificateId(String certificateId);
    List<Student> findByStatus(Student.CertificateStatus status);
    
    boolean existsByStudentId(String studentId);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT s.universityName as department, " +
           "COUNT(CASE WHEN s.status = 'PENDING' THEN 1 END) as pending, " +
           "COUNT(CASE WHEN s.status = 'QUEUED' THEN 1 END) as queued, " +
           "COUNT(CASE WHEN s.status = 'ISSUED' THEN 1 END) as issued " +
           "FROM Student s GROUP BY s.universityName")
    List<DepartmentStats> getDepartmentStatistics();
    
    interface DepartmentStats {
        String getDepartment();
        Long getPending();
        Long getQueued();
        Long getIssued();
    }
    
    @Query("SELECT " +
           "COUNT(CASE WHEN s.status = 'PENDING' THEN 1 END) as pending, " +
           "COUNT(CASE WHEN s.status = 'QUEUED' THEN 1 END) as queued, " +
           "COUNT(CASE WHEN s.status = 'ISSUED' THEN 1 END) as issued " +
           "FROM Student s")
    StatusCounts getStatusCounts();
    
    interface StatusCounts {
        Long getPending();
        Long getQueued();
        Long getIssued();
    }
}