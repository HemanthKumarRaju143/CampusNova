package com.KumarCollege.Pojo.StudentEnrollment.Response;

import com.KumarCollege.Enum.StudentEnrollmentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnrollmentResponse {
    private Long enrollmentId;
    private Long studentId;
    private Long classId;
    private LocalDate enrollmentDate;
    private StudentEnrollmentStatus status;
}
