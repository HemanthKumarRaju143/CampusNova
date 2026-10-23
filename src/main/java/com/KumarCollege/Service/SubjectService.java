package com.KumarCollege.Service;

import com.KumarCollege.Enum.SubjectStatus;
import com.KumarCollege.Pojo.Subjects.Requests.ChangeSubjectStatusRequest;
import com.KumarCollege.Pojo.Subjects.Requests.CreateSubjectRequest;
import com.KumarCollege.Pojo.Subjects.Response.SubjectResponse;

import org.springframework.data.domain.Page;

import java.util.List;

public interface SubjectService {

    List<SubjectResponse> createSubjects(
            List<CreateSubjectRequest> requests
    );

    Page<SubjectResponse> getAllSubjects(
            SubjectStatus status,
            int page,
            int size
    );

    SubjectResponse getSubjectById(Long subjectId);

    SubjectResponse replaceSubject(
            Long subjectId,
            CreateSubjectRequest request
    );

    SubjectResponse changeSubjectStatus(
            Long subjectId,
            ChangeSubjectStatusRequest request
    );
}