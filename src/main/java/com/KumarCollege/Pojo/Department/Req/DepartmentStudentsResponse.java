package com.KumarCollege.Pojo.Department.Req;

import com.KumarCollege.Pojo.Course.Response.ProgramStudentsResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({
        "departmentId",
        "departmentName",
        "courses"
})
public class DepartmentStudentsResponse {
    private Long departmentId;
    private String departmentName;
    private List<ProgramStudentsResponse> programs;
}
