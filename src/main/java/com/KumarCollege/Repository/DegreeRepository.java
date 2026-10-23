package com.KumarCollege.Repository;

import com.KumarCollege.Entity.Degree;
import com.KumarCollege.Enum.DegreeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DegreeRepository extends JpaRepository<Degree, Long> {

    Optional<Degree> findByDegreeCodeIgnoreCase(String degreeCode);

    boolean existsByDegreeCodeIgnoreCase(String degreeCode);

    boolean existsByDegreeNameIgnoreCase(String degreeName);

    List<Degree> findByLevel(DegreeStatus status);

    Page<Degree> findByLevel(DegreeStatus status, Pageable pageable);

    Page<Degree> findByDegreeNameContainingIgnoreCase(
            String degreeName,
            Pageable pageable
    );

    boolean existsByDegreeCodeIgnoreCaseAndDegreeIdNot(
            String degreeCode,
            Long degreeId
    );

    boolean existsByDegreeNameIgnoreCaseAndDegreeIdNot(
            String degreeName,
            Long degreeId
    );
}