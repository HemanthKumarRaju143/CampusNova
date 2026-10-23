
package com.KumarCollege.Validation;

import com.KumarCollege.Entity.CollegeClass;
import com.KumarCollege.Repository.CollegeClassRepository;
import com.KumarCollege.Repository.ProgramRepository; // CHANGED
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class ClassValidation {

    // CHANGED: CourseRepository -> ProgramRepository
    private final ProgramRepository programRepository;

    private final CollegeClassRepository collegeClassRepository;

    // CHANGED: Validate programId.
    public void validateProgram(Long programId) {

        if (!programRepository.existsById(programId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Program not found with ID: " + programId
            );
        }
    }

    public CollegeClass findClass(Long id) {

        return collegeClassRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Class not found with ID: " + id
                ));
    }

    public void validatePagination(int page, int size) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be zero or greater"
            );
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100"
            );
        }
    }
}
