package com.KumarCollege.Pojo.StudentEnrollment.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReplaceEnrollmentRequest {
    @NotNull(message = "Student ID is required")
    @Positive(message = "Student ID must be positive")
    private Long studentId;

    @NotNull(message = "Class ID is required")
    @Positive(message = "Class ID must be positive")
    private Long classId;

    @NotNull(message = "Enrollment date is required")
    private LocalDate enrollmentDate;
}
