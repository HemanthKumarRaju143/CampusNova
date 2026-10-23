package com.KumarCollege.Pojo.Course.Req;

import com.KumarCollege.Enum.ProgramStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReplaceProgramRequest {
    @NotNull(message = "Department ID is required")
    @Positive(message = "Department ID must be positive")
    private Long departmentId;

    // CHANGED: Added degreeId.
    @NotNull(message = "Degree ID is required")
    @Positive(message = "Degree ID must be positive")
    private Long degreeId;

    @NotBlank(message = "Program code is required")
    @Size(max = 30, message = "Program code cannot exceed 30 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9_-]+$",
            message = "Program code can contain letters, numbers, underscores and hyphens only"
    )
    private String programCode;

    @NotBlank(message = "Program name is required")
    @Size(max = 150, message = "Program name cannot exceed 150 characters")
    private String programName;

    @NotNull(message = "Program status is required")
    private ProgramStatus status; // CHANGED: CourseStatus -> ProgramStatus
}
