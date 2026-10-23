package com.KumarCollege.Pojo.Student.Req;

import com.KumarCollege.Enum.StudentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStatusRequest {

    @NotNull(message = "Student status is required") // CHANGED: status must not be null
    private StudentStatus status;
}