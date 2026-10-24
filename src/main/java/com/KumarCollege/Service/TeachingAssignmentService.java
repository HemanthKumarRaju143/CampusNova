package com.KumarCollege.Service;

import com.KumarCollege.Enum.AssignmentStatus;
import com.KumarCollege.Pojo.TeachingAssignment.Request.ChangeTeachingAssignmentStatusRequest;
import com.KumarCollege.Pojo.TeachingAssignment.Request.CreateTeachingAssignmentRequest;
import com.KumarCollege.Pojo.TeachingAssignment.Response.TeachingAssignmentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TeachingAssignmentService {

    List<TeachingAssignmentResponse> createBulk(
            List<CreateTeachingAssignmentRequest> requests
    );

    Page<TeachingAssignmentResponse> getAll(
            Long classId,
            Long subjectId,
            Long staffId,
            AssignmentStatus status,
            int page,
            int size
    );

    TeachingAssignmentResponse getById(Long id);

    TeachingAssignmentResponse changeStatus(
            Long id,
            ChangeTeachingAssignmentStatusRequest request
    );
}