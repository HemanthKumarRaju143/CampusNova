package com.KumarCollege.Repository;

import com.KumarCollege.Entity.StudentClassEnrollment;
import com.KumarCollege.Enum.StudentEnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentClassEnrollmentRepository
        extends JpaRepository<StudentClassEnrollment, Long> {

    boolean existsByStudentIdAndClassId(
            Long studentId,
            Long classId
    );

    boolean existsByStudentIdAndClassIdAndEnrollmentIdNot(
            Long studentId,
            Long classId,
            Long enrollmentId
    );

    Page<StudentClassEnrollment> findByStatus(
            StudentEnrollmentStatus status,
            Pageable pageable
    );

    Page<StudentClassEnrollment> findByStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<StudentClassEnrollment> findByClassId(
            Long classId,
            Pageable pageable
    );

    Page<StudentClassEnrollment> findByStudentIdAndStatus(
            Long studentId,
            StudentEnrollmentStatus status,
            Pageable pageable
    );

    Page<StudentClassEnrollment> findByClassIdAndStatus(
            Long classId,
            StudentEnrollmentStatus status,
            Pageable pageable
    );


    @Query(value = """
    SELECT
        p.program_id,
        p.program_name,
        s.student_id,
        CONCAT_WS(' ', s.first_name, s.last_name) AS student_name,
        s.admission_number,
        s.email,
        c.section,
        s.status AS student_status,
        e.status AS enrollment_status
    FROM student_class_enrollments e
    JOIN students s
        ON s.student_id = e.student_id
    JOIN classes c
        ON c.class_id = e.class_id
    JOIN programs p
        ON p.program_id = c.program_id
    WHERE p.program_id = :programId
      AND (:studentStatus IS NULL OR s.status = :studentStatus)
      AND (:enrollmentStatus IS NULL OR e.status = :enrollmentStatus)
    ORDER BY s.student_id
    """, nativeQuery = true)
    List<Object[]> findProgramStudents(
            @Param("programId") Long programId, // CHANGED: courseId -> programId
            @Param("studentStatus") String studentStatus,
            @Param("enrollmentStatus") String enrollmentStatus
    );

    @Query(value = """
    SELECT
        p.program_id,
        p.program_name,
        s.student_id,
        CONCAT_WS(' ', s.first_name, s.last_name) AS student_name,
        s.admission_number,
        s.email,
        c.section,
        s.status AS student_status,
        e.status AS enrollment_status
    FROM programs p
    JOIN classes c
        ON c.program_id = p.program_id  -- CHANGED: old c.course_id join
    JOIN student_class_enrollments e
        ON e.class_id = c.class_id
    JOIN students s
        ON s.student_id = e.student_id
    WHERE p.program_id = :programId    -- CHANGED: old co.course_id
      AND c.section = :section
    ORDER BY s.student_id
    """, nativeQuery = true)
    List<Object[]> findStudentsByProgramAndSection(
            @Param("programId") Long programId, // CHANGED
            @Param("section") String section
    );


    //List<StudentClassEnrollment> findByClassIdIn(List<Long> classIds);
}