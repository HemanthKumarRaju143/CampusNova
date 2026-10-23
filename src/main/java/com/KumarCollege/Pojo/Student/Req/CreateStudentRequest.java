package com.KumarCollege.Pojo.Student.Req;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateStudentRequest {

    // CHANGED: courseId/departmentId -> programId
    @NotNull(message = "Program ID is required")
    @Positive(message = "Program ID must be positive")
    private Long programId;

    @NotBlank(message = "Admission number is required")
    @Size(max = 30, message = "Admission number cannot exceed 30 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9/-]+$",
            message = "Admission number can contain letters, numbers, / and - only"
    )
    private String admissionNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 60, message = "First name cannot exceed 60 characters")
    private String firstName; // CHANGED: max 50 -> 60 to match SQL

    @Size(max = 60, message = "Last name cannot exceed 60 characters")
    private String lastName; // Optional, matching SQL

    // Kept required as an API rule, although the SQL column permits NULL.
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid") // CHANGED: use standard email validation
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email; // CHANGED: max 100 -> 150

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String mobile;

    // Kept required as an API rule, although SQL permits NULL.
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Admission year is required")
    @Min(value = 2000, message = "Admission year cannot be before 2000")
    @Max(value = 2100, message = "Admission year is invalid")
    private Short admissionYear;
}