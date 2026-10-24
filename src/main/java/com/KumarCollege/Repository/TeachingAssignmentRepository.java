package com.KumarCollege.Repository;

import com.KumarCollege.Entity.TeachingAssignment;
import com.KumarCollege.Enum.AssignmentStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeachingAssignmentRepository
        extends JpaRepository<TeachingAssignment, Long> {

    // Check duplicate assignment
    boolean existsByClassIdAndSubjectIdAndStaffId(
            Long classId,
            Long subjectId,
            Long staffId
    );

    // Get assignments with optional filters and pagination
    @Query("""
        SELECT ta
        FROM TeachingAssignment ta
        WHERE (:classId IS NULL OR ta.classId = :classId)
          AND (:subjectId IS NULL OR ta.subjectId = :subjectId)
          AND (:staffId IS NULL OR ta.staffId = :staffId)
          AND (:status IS NULL OR ta.status = :status)
        """)
    Page<TeachingAssignment> findFiltered(
            @Param("classId") Long classId,
            @Param("subjectId") Long subjectId,
            @Param("staffId") Long staffId,
            @Param("status") AssignmentStatus status,
            Pageable pageable
    );
}