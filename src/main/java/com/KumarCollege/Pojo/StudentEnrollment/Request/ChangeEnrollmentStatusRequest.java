package com.KumarCollege.Pojo.StudentEnrollment.Request;

import com.KumarCollege.Enum.StudentEnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeEnrollmentStatusRequest {
    @NotNull(message = "Enrollment status is required")
    private StudentEnrollmentStatus status;
}
