package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.Subject;
import com.KumarCollege.Enum.SubjectStatus;

import com.KumarCollege.Pojo.Subjects.Requests.ChangeSubjectStatusRequest;
import com.KumarCollege.Pojo.Subjects.Requests.CreateSubjectRequest;
import com.KumarCollege.Pojo.Subjects.Response.SubjectResponse;

import com.KumarCollege.Repository.SubjectRepository;
import com.KumarCollege.Service.SubjectService;
import com.KumarCollege.Validation.SubjectValidation;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectValidation subjectValidation;

    @Override
    public List<SubjectResponse> createSubjects(
            List<CreateSubjectRequest> requests) {

        // FIXED: Reject null or empty bulk requests.
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one subject is required"
            );
        }

        // CHANGED: Subject codes are globally unique in the new schema.
        // This set catches duplicates inside the same request.
        Set<String> requestCodes = new HashSet<>();

        // STEP 1: Validate every request before saving anything.
        for (CreateSubjectRequest request : requests) {

            String code = request.getSubjectCode().trim().toUpperCase();

            if (!requestCodes.add(code)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate subject code in request: " + code
                );
            }

            // CHANGED: Validate subject code using the new method.
            subjectValidation.validateUniqueSubject(code, null);

            // CHANGED: Validate departmentId instead of courseId.
            subjectValidation.validateDepartmentExists(
                    request.getDepartmentId()
            );
        }

        // STEP 2: Convert request DTOs into entities.
        List<Subject> subjects = new ArrayList<>();

        for (CreateSubjectRequest request : requests) {

            Subject subject = new Subject();

            // CHANGED: courseId -> departmentId.
            subject.setDepartmentId(request.getDepartmentId());

            subject.setSubjectCode(
                    request.getSubjectCode().trim().toUpperCase()
            );

            subject.setSubjectName(request.getSubjectName().trim());

            // CHANGED: Map the new required subject type.
            subject.setSubjectType(request.getSubjectType());

            subject.setCredits(request.getCredits());

            // New subjects start as ACTIVE.
            subject.setStatus(SubjectStatus.ACTIVE);

            subjects.add(subject);
        }

        // STEP 3: Save all subjects after validation.
        List<Subject> savedSubjects = subjectRepository.saveAll(subjects);

        // STEP 4: Convert entities into response DTOs.
        return savedSubjects.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectResponse> getAllSubjects(
            SubjectStatus status,
            int page,
            int size) {

        subjectValidation.validatePage(page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Subject> subjects = (status == null)
                ? subjectRepository.findAll(pageable)
                : subjectRepository.findByStatus(status, pageable);

        return subjects.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Long subjectId) {

        Subject subject = subjectValidation.findSubject(subjectId);

        return toResponse(subject);
    }

    @Override
    public SubjectResponse replaceSubject(
            Long subjectId,
            CreateSubjectRequest request) {

        Subject subject = subjectValidation.findSubject(subjectId);

        // CHANGED: Validate the new department reference.
        subjectValidation.validateDepartmentExists(
                request.getDepartmentId()
        );

        // FIXED: Exclude this subject's ID when checking its code.
        subjectValidation.validateUniqueSubject(
                request.getSubjectCode(),
                subjectId
        );

        subject.setDepartmentId(request.getDepartmentId());

        subject.setSubjectCode(
                request.getSubjectCode().trim().toUpperCase()
        );

        subject.setSubjectName(request.getSubjectName().trim());

        // CHANGED: Update subjectType instead of semester.
        subject.setSubjectType(request.getSubjectType());

        subject.setCredits(request.getCredits());

        // Keep the existing status; the PATCH endpoint changes it.
        // MySQL manages created_at and updated_at.

        return toResponse(subjectRepository.saveAndFlush(subject));
    }

    @Override
    public SubjectResponse changeSubjectStatus(
            Long subjectId,
            ChangeSubjectStatusRequest request) {

        Subject subject = subjectValidation.findSubject(subjectId);

        subject.setStatus(request.getStatus());

        return toResponse(subjectRepository.saveAndFlush(subject));
    }

    private SubjectResponse toResponse(Subject subject) {

        SubjectResponse response = new SubjectResponse();

        response.setSubjectId(subject.getSubjectId());

        // CHANGED: courseId -> departmentId.
        response.setDepartmentId(subject.getDepartmentId());

        response.setSubjectCode(subject.getSubjectCode());
        response.setSubjectName(subject.getSubjectName());

        // CHANGED: Map subjectType; removed semester.
        response.setSubjectType(subject.getSubjectType());

        response.setCredits(subject.getCredits());
        response.setStatus(subject.getStatus());

        // CHANGED: Include the timestamps in the response.
        response.setCreatedAt(subject.getCreatedAt());
        response.setUpdatedAt(subject.getUpdatedAt());

        return response;
    }
}