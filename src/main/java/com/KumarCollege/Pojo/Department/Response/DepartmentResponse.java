package com.KumarCollege.Pojo.Department.Response;

import com.KumarCollege.Enum.DepartmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentResponse {
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private String hodName;
    private DepartmentStatus status;
    //private DepartmentStatus status;
}
