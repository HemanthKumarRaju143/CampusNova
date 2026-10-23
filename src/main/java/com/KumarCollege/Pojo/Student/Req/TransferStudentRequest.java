package com.KumarCollege.Pojo.Student.Req;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TransferStudentRequest {

    @NotNull(message = "New program ID is required")
    @Positive(message = "New program ID must be positive")
    private Long newProgramId;

    // CHANGED: A transfer requires a new admission number.
    @NotBlank(message = "New admission number is required")
    @Size(max = 30, message = "Admission number cannot exceed 30 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9/-]+$",
            message = "Admission number can contain letters, numbers, / and - only"
    )
    private String newAdmissionNumber;

    // CHANGED: The new admission record needs its admission year.
    @NotNull(message = "New admission year is required")
    @Min(value = 2000, message = "Admission year cannot be before 2000")
    @Max(value = 2100, message = "Admission year is invalid")
    private Short newAdmissionYear;
}