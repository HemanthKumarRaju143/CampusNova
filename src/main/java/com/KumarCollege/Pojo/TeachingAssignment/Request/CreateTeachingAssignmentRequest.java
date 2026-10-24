package com.KumarCollege.Pojo.TeachingAssignment.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateTeachingAssignmentRequest {

    @NotNull(message = "Class ID is required")
    @Positive(message = "Class ID must be positive")
    private Long classId;

    @NotNull(message = "Subject ID is required")
    @Positive(message = "Subject ID must be positive")
    private Long subjectId;

    @NotNull(message = "Staff ID is required")
    @Positive(message = "Staff ID must be positive")
    private Long staffId;
}