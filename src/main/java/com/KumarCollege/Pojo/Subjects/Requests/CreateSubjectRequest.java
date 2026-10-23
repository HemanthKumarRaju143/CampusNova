package com.KumarCollege.Pojo.Subjects.Requests;

import com.KumarCollege.Enum.SubjectType;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSubjectRequest {

    // CHANGED: Replaced courseId with departmentId.
    @NotNull(message = "Department ID is required")
    @Positive(message = "Department ID must be greater than 0")
    private Long departmentId;

    @NotBlank(message = "Subject code is required")
    @Size(max = 30, message = "Subject code cannot exceed 30 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9_-]+$",
            message = "Subject code can contain only letters, numbers, hyphens and underscores"
    )
    private String subjectCode;

    @NotBlank(message = "Subject name is required")
    @Size(max = 120, message = "Subject name cannot exceed 120 characters")
    private String subjectName;

    // CHANGED: Added because subject_type is NOT NULL in SQL.
    @NotNull(message = "Subject type is required")
    private SubjectType subjectType;

    @NotNull(message = "Credits are required")
    @DecimalMin(value = "0.00", message = "Credits cannot be negative")
    @Digits(
            integer = 2,
            fraction = 2,
            message = "Credits must have at most 2 integer digits and 2 decimal places"
    )
    private BigDecimal credits;
}