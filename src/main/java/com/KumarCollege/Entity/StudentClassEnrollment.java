
package com.KumarCollege.Entity;

import com.KumarCollege.Enum.StudentEnrollmentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "student_class_enrollments")
public class StudentClassEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id") // CHANGED: Explicit SQL column mapping
    private Long enrollmentId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false) // CHANGED: Explicit mapping
    private StudentEnrollmentStatus status = StudentEnrollmentStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false) // CHANGED
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false) // CHANGED
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
