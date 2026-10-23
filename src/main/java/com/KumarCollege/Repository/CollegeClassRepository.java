
package com.KumarCollege.Repository;

import com.KumarCollege.Entity.CollegeClass;
import com.KumarCollege.Enum.ClassStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollegeClassRepository
        extends JpaRepository<CollegeClass, Long> {

    Page<CollegeClass> findByStatus(
            ClassStatus status,
            Pageable pageable
    );

    // CHANGED: Find classes by program instead of course.
    Page<CollegeClass> findByProgramId(
            Long programId,
            Pageable pageable
    );

    Page<CollegeClass> findByProgramIdAndStatus(
            Long programId,
            ClassStatus status,
            Pageable pageable
    );
}
