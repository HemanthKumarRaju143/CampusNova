package com.KumarCollege.Entity;

import com.KumarCollege.Enum.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "attendance_records")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false)
    // CHANGED: Match SQL's default value.
    private AttendanceStatus attendanceStatus = AttendanceStatus.PRESENT;

    @Column(name = "marked_at", nullable = false, updatable = false)
    private LocalDateTime markedAt;

    @PrePersist
    protected void onCreate() {
        // CHANGED: Avoid overwriting a timestamp supplied by the application.
        if (markedAt == null) {
            markedAt = LocalDateTime.now();
        }
    }
}