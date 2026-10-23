package com.KumarCollege.Pojo.Department.Req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDepartmentReq {

    @NotBlank(message = "Department code is required")
    @Size(max = 20, message = "Department code must not exceed 20 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9_-]+$",
            message = "Department code can contain letters, numbers, _ and - only"
    )
    private String departmentCode;

    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String departmentName;

    @Size(max = 100, message = "HOD name must not exceed 100 characters")
    private String hodName;
}
