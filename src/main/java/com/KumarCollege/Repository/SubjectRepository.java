package com.KumarCollege.Repository;

import com.KumarCollege.Entity.Subject;
import com.KumarCollege.Enum.SubjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject , Long> {
    boolean existsBySubjectCodeIgnoreCase(String subjectCode);

    // CHANGED: Excludes the current subject when updating.
    boolean existsBySubjectCodeIgnoreCaseAndSubjectIdNot(
            String subjectCode,
            Long subjectId
    );

    // FIXED: Removed courseId + semester + subjectName methods.
    // The new subjects table has neither course_id nor semester.

    Page<Subject> findByStatus(
            SubjectStatus status,
            Pageable pageable
    );
}
