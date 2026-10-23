package com.KumarCollege.Entity;

import com.KumarCollege.Enum.AttendanceSessionStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "attendance_sessions")
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    // CHANGED: SQL column is staff_id, not teacher_id.
    private Long staffId;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_number", nullable = false)
    private Byte sessionNumber = 1;

    // CHANGED: Explicitly map the SQL column and match its default.
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceSessionStatus status = AttendanceSessionStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        // CHANGED: Ensure the required date has a value before insertion.
        if (sessionDate == null) {
            throw new IllegalStateException("sessionDate must not be null");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}