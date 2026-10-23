package com.KumarCollege.Entity;

import com.KumarCollege.Enum.SubjectStatus;
import com.KumarCollege.Enum.SubjectType;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subjects")
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    // CHANGED: course_id -> department_id.
    // Your new subjects table has a foreign key to departments.
    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "subject_code", nullable = false, unique = true, length = 30)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 120)
    private String subjectName;

    // CHANGED: Added subject_type to match the SQL table.
    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type", nullable = false, length = 7)
    private SubjectType subjectType;

    // FIXED: Matches DECIMAL(4,2) and CHECK (credits >= 0).
    @Column(name = "credits", nullable = false, precision = 4, scale = 2)
    private BigDecimal credits = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 8)
    private SubjectStatus status = SubjectStatus.ACTIVE;

    // CHANGED: MySQL creates the timestamp using DEFAULT CURRENT_TIMESTAMP.
    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    // CHANGED: MySQL updates this column using ON UPDATE CURRENT_TIMESTAMP.
    @Column(
            name = "updated_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime updatedAt;
}