package com.KumarCollege.Repository;

import com.KumarCollege.Entity.ProgramCurriculum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramCurriculumRepository
        extends JpaRepository<ProgramCurriculum, Long> {

    boolean existsByProgramIdAndSubjectIdAndSemester(
            Long programId,
            Long subjectId,
            Byte semester
    );

    // CHANGED: Use c.mandatory, NOT c.isMandatory
    @Query("""
        SELECT c FROM ProgramCurriculum c
        WHERE (:programId IS NULL OR c.programId = :programId)
          AND (:subjectId IS NULL OR c.subjectId = :subjectId)
          AND (:semester IS NULL OR c.semester = :semester)
          AND (:isMandatory IS NULL OR c.mandatory = :isMandatory)
        """)
    Page<ProgramCurriculum> searchCurriculum(
            @Param("programId") Long programId,
            @Param("subjectId") Long subjectId,
            @Param("semester") Byte semester,
            @Param("isMandatory") Boolean isMandatory,
            Pageable pageable
    );

    List<ProgramCurriculum> findAllByCurriculumIdIn(List<Long> ids);
}