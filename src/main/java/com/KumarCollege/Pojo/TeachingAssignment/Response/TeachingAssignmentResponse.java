package com.KumarCollege.Pojo.TeachingAssignment.Response;

import com.KumarCollege.Enum.AssignmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TeachingAssignmentResponse {

    private Long assignmentId;

    private Long classId;
    private Long subjectId;
    private Long staffId;

    private AssignmentStatus status;

    private LocalDateTime assignedAt;
}