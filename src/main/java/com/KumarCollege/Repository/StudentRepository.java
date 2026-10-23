package com.KumarCollege.Repository;

import com.KumarCollege.Entity.Student;
import com.KumarCollege.Enum.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student , Long> {

    boolean existsByAdmissionNumberIgnoreCase(String admissionNumber);

    boolean existsByAdmissionNumberIgnoreCaseAndStudentIdNot(
            String admissionNumber,
            Long studentId
    );

    boolean existsByAdmissionNumberIgnoreCaseAndStudentIdAndStatusNot(String admissionNumber , Long studentId , StudentStatus status);

    Page<Student> findByStatus(StudentStatus status , Pageable pageable);
}
