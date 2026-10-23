package com.KumarCollege.Pojo.Student.Response;

import com.KumarCollege.Enum.StudentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentResponse {
    private Long studentId;

    private Long programId;
    private String admissionNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private LocalDate dateOfBirth;
    private Short admissionYear;
    private StudentStatus status;
}
