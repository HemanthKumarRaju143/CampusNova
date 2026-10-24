package com.KumarCollege.Entity;

import com.KumarCollege.Enum.StudentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id") // CHANGED: Explicit mapping to SQL primary key
    private Long studentId;

    // CHANGED: Removed departmentId and courseId.
    // The SQL students table contains program_id.
    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Column(name = "admission_number", nullable = false, unique = true, length = 30)
    private String admissionNumber;

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName; // CHANGED: Explicit SQL column mapping

    @Column(name = "last_name", length = 60)
    private String lastName; // Optional in SQL

    @Column(name = "email", length = 150)
    private String email; // CHANGED: SQL allows NULL

    @Column(name = "phone", length = 20)
    private String mobile; // Java field is mobile; SQL column is phone

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "admission_year", nullable = false)
    private Short admissionYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StudentStatus status = StudentStatus.ACTIVE;

    // CHANGED: Hibernate manages these timestamps.
    // Explicit column names match the SQL table.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}