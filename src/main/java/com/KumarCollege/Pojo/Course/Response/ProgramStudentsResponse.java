package com.KumarCollege.Pojo.Course.Response;

import lombok.Data;

import java.util.List;

@Data
public class ProgramStudentsResponse {

    private Long programId;

    private String programName;

    private List<ProgramStudentResponse> students;

}
