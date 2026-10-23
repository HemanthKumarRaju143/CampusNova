package com.KumarCollege.Service;

import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Pojo.Course.Response.ProgramStudentResponse;
import com.KumarCollege.Pojo.StudentEnrollment.Request.ChangeEnrollmentStatusRequest;
import com.KumarCollege.Pojo.StudentEnrollment.Request.CreateEnrollmentRequest;
import com.KumarCollege.Pojo.StudentEnrollment.Request.ReplaceEnrollmentRequest;
import com.KumarCollege.Pojo.StudentEnrollment.Response.EnrollmentResponse;

import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentClassEnrollmentService {

    List<EnrollmentResponse> createBulkEnrollments(
            List<CreateEnrollmentRequest> requests
    );

    Page<EnrollmentResponse> getAllEnrollments(
            StudentEnrollmentStatus status,
            int page,
            int size
    );

    EnrollmentResponse getEnrollmentById(Long id);

    EnrollmentResponse replaceEnrollment(
            Long id,
            ReplaceEnrollmentRequest request
    );

    EnrollmentResponse changeEnrollmentStatus(
            Long id,
            ChangeEnrollmentStatusRequest request
    );

    Page<EnrollmentResponse> getEnrollmentsByStudent(
            Long studentId,
            StudentEnrollmentStatus status,
            int page,
            int size
    );

    Page<EnrollmentResponse> getEnrollmentsByClass(
            Long classId,
            StudentEnrollmentStatus status,
            int page,
            int size
    );

    List<ProgramStudentResponse> getStudentsByProgramAndSection(
            Long programId, // CHANGED: courseId -> programId
            String section
    );
}