package com.KumarCollege.Pojo.Subjects.Response;

import com.KumarCollege.Enum.SubjectStatus;
import com.KumarCollege.Enum.SubjectType;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SubjectResponse {

    private Long subjectId;

    private Long departmentId;

    private String subjectCode;
    private String subjectName;

    private SubjectType subjectType;

    private BigDecimal credits;
    private SubjectStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}