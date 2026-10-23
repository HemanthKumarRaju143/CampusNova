package com.KumarCollege.Entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "program_curriculum")
public class ProgramCurriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curriculum_id")
    private Long curriculumId;

    @JoinColumn(name = "program_id", nullable = false)
    private Long programId;

    @JoinColumn(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(nullable = false)
    private Byte semester;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean mandatory = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
