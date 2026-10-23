package com.KumarCollege.Repository;

import com.KumarCollege.Entity.Department;
import com.KumarCollege.Enum.DepartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department , Long> {
    boolean existsByDepartmentNameIgnoreCaseAndDepartmentCodeAndStatus(
            String departmentName,
            String departmentCode,
            DepartmentStatus status
    );

    List<Department> findByStatus(DepartmentStatus status);

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
        JOIN departments d
            ON d.department_id = p.department_id
        JOIN students s
            ON s.program_id = p.program_id
        JOIN student_class_enrollments e
            ON e.student_id = s.student_id
        JOIN classes c
            ON c.class_id = e.class_id
        WHERE d.department_id = :departmentId
          AND (:studentStatus IS NULL OR s.status = :studentStatus)
          AND (:enrollmentStatus IS NULL OR e.status = :enrollmentStatus)
        ORDER BY p.program_id, s.student_id
        """, nativeQuery = true)
    List<Object[]> findDepartmentStudents(
            @Param("departmentId") Long departmentId,
            @Param("studentStatus") String studentStatus,
            @Param("enrollmentStatus") String enrollmentStatus
    );
}
