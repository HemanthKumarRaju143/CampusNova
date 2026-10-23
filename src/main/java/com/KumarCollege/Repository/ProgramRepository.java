package com.KumarCollege.Repository;

import com.KumarCollege.Entity.Program;
import com.KumarCollege.Enum.ProgramStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program , Long> {
    boolean existsByDegreeIdAndProgramCodeIgnoreCase(
            Long degreeId,
            String programCode
    );

    // CHANGED: When updating, exclude the current program.
    boolean existsByDegreeIdAndProgramCodeIgnoreCaseAndProgramIdNot(
            Long degreeId,
            String programCode,
            Long programId
    );

    Page<Program> findByStatus(ProgramStatus status, Pageable pageable);
}
