package com.KumarCollege.Pojo.Subjects.Requests;

import com.KumarCollege.Enum.SubjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeSubjectStatusRequest {
    @NotNull(message = "Subject status is required")
    private SubjectStatus status;
}
