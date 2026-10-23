package com.KumarCollege.Pojo.Class.Req;

import com.KumarCollege.Enum.ClassStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeClassStatusRequest {
    @NotNull(message = "Status is required")
    private ClassStatus status;
}
