package com.KumarCollege.Pojo.TeachingAssignment.Request;

import com.KumarCollege.Enum.AssignmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeTeachingAssignmentStatusRequest {

    @NotNull(message = "Assignment status is required")
    private AssignmentStatus status;
}