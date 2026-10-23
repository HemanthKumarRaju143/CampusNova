package com.KumarCollege.Pojo.Department.Req;

import com.KumarCollege.Enum.DepartmentStatus;
import lombok.Data;

@Data
public class UpdateDepartmentStatus {
    private DepartmentStatus status;
}
