package com.KumarCollege.Validation;

import com.KumarCollege.Entity.Subject;
import com.KumarCollege.Repository.DepartmentRepository;
import com.KumarCollege.Repository.SubjectRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class SubjectValidation {

    private final SubjectRepository subjectRepository;

    // CHANGED: ProgramRepository is no longer used here.
    // Subjects now reference departments, so use DepartmentRepository.
    private final DepartmentRepository departmentRepository;

    public void validateUniqueSubject(
            String subjectCode,
            Long existingSubjectId) {

        String code = subjectCode.trim();

        // FIXED: When updating, do not reject the subject's own code.
        boolean duplicateCode;

        if (existingSubjectId == null) {
            duplicateCode =
                    subjectRepository.existsBySubjectCodeIgnoreCase(code);
        } else {
            duplicateCode =
                    subjectRepository.existsBySubjectCodeIgnoreCaseAndSubjectIdNot(
                            code,
                            existingSubjectId
                    );
        }

        if (duplicateCode) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Subject code already exists: " + code
            );
        }
    }

    // CHANGED: Replaces validateCourseExists().
    public void validateDepartmentExists(Long departmentId) {

        if (departmentId == null || departmentId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Department ID must be positive"
            );
        }

        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException(
                    "Department not found with ID: " + departmentId
            );
        }
    }

    public Subject findSubject(Long subjectId) {

        // FIXED: Reject invalid IDs before querying the database.
        if (subjectId == null || subjectId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Subject ID must be positive"
            );
        }

        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject not found with ID: " + subjectId
                ));
    }

    public void validatePage(int page, int size) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative"
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