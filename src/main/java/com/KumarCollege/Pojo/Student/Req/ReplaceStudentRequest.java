package com.KumarCollege.Pojo.Student.Req;

import com.KumarCollege.Enum.StudentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReplaceStudentRequest {

    // CHANGED: Removed departmentId and courseId.
    @NotNull(message = "Program ID is required")
    @Positive(message = "Program ID must be positive")
    private Long programId;

    @NotBlank(message = "Admission number is required")
    @Size(max = 30, message = "Admission number cannot exceed 30 characters")
    @Pattern(regexp = "^[A-Za-z0-9/-]+$")
    private String admissionNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 60)
    private String firstName;

    @Size(max = 60)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$")
    private String mobile;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Admission year is required")
    @Min(2000)
    @Max(2100)
    private Short admissionYear;

    @NotNull(message = "Student status is required")
    private StudentStatus status;
}