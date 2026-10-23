package com.KumarCollege.Pojo.ProgramCurriculum.Req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateCurriculumRequest {
    @NotNull(message = "Program ID is required")
    @Positive(message = "Program ID must be positive")
    private Long programId;

    @NotNull(message = "Subject ID is required")
    @Positive(message = "Subject ID must be positive")
    private Long subjectId;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 12")
    @Max(value = 12, message = "Semester must be between 1 and 12")
    private Byte semester;

    @NotNull(message = "Mandatory status is required")
    private Boolean isMandatory = true;
}
