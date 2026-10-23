package com.KumarCollege.Entity;

import com.KumarCollege.Enum.DegreeStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "degrees")
public class Degree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "degree_id")
    private Long degreeId;

    @Column(name = "degree_code", nullable = false, unique = true, length = 20)
    private String degreeCode;

    @Column(name = "degree_name", nullable = false, unique = true, length = 50)
    private String degreeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private DegreeStatus level;

    @Column(name = "duration_years", nullable = false)
    private Integer durationYears;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}