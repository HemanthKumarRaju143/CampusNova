package com.KumarCollege.Pojo.Course.Response;

import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Enum.StudentStatus;
import lombok.Data;

@Data
public class ProgramStudentResponse {
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private String email;
    private String section;
    private StudentStatus studentStatus;
    private StudentEnrollmentStatus enrollmentStatus;
}
