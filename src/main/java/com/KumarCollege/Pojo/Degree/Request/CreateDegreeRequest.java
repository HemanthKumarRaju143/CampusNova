package com.KumarCollege.Pojo.Degree.Request;

import com.KumarCollege.Enum.DegreeStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateDegreeRequest {
    @NotBlank(message = "Degree code is required")
    @Size(max = 20, message = "Degree code must not exceed 20 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9_-]+$",
            message = "Degree code can contain letters, numbers, _ and - only"
    )
    private String degreeCode;

    @NotBlank(message = "Degree name is required")
    @Size(max = 50, message = "Degree name must not exceed 50 characters")
    private String degreeName;

    @NotNull(message = "Degree level is required")
    private DegreeStatus status;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 year")
    @Max(value = 15, message = "Duration cannot exceed 15 years")
    private Integer durationYears;
}
