package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.CollegeClass;
import com.KumarCollege.Entity.TeachingAssignment;
import com.KumarCollege.Enum.AssignmentStatus;
import com.KumarCollege.Pojo.TeachingAssignment.Request.ChangeTeachingAssignmentStatusRequest;
import com.KumarCollege.Pojo.TeachingAssignment.Request.CreateTeachingAssignmentRequest;
import com.KumarCollege.Pojo.TeachingAssignment.Response.TeachingAssignmentResponse;
import com.KumarCollege.Repository.CollegeClassRepository;
import com.KumarCollege.Repository.StaffRepository;
import com.KumarCollege.Repository.SubjectRepository;
import com.KumarCollege.Repository.TeachingAssignmentRepository;
import com.KumarCollege.Service.TeachingAssignmentService;

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
public class TeachingAssignmentServiceImpl
        implements TeachingAssignmentService {

    private final TeachingAssignmentRepository assignmentRepository;
    private final CollegeClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final StaffRepository staffRepository;

    // CREATE MULTIPLE ASSIGNMENTS
    @Override
    @Transactional
    public List<TeachingAssignmentResponse> createBulk(
            List<CreateTeachingAssignmentRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one teaching assignment is required"
            );
        }

        // Prevent duplicate combinations within the request.
        Set<String> requestKeys = new HashSet<>();

        for (CreateTeachingAssignmentRequest request : requests) {

            String key = request.getClassId() + "-"
                    + request.getSubjectId() + "-"
                    + request.getStaffId();

            if (!requestKeys.add(key)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate assignment in request: " + key
                );
            }

            // Prevent combinations that already exist in the database.
            boolean exists =
                    assignmentRepository.existsByClassIdAndSubjectIdAndStaffId(
                            request.getClassId(),
                            request.getSubjectId(),
                            request.getStaffId()
                    );

            if (exists) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Teaching assignment already exists: " + key
                );
            }
        }

        List<TeachingAssignment> assignments = new ArrayList<>();

        for (CreateTeachingAssignmentRequest request : requests) {

            // Validate that the referenced records exist.
            if (!classRepository.existsById(request.getClassId())) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Class not found: " + request.getClassId()
                );
            }

            if (!subjectRepository.existsById(request.getSubjectId())) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Subject not found: " + request.getSubjectId()
                );
            }

            if (!staffRepository.existsById(request.getStaffId())) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Staff not found: " + request.getStaffId()
                );
            }

            TeachingAssignment assignment = new TeachingAssignment();

            assignment.setClassId(request.getClassId());
            assignment.setSubjectId(request.getSubjectId());
            assignment.setStaffId(request.getStaffId());
            assignment.setStatus(AssignmentStatus.ACTIVE);

            // assignedAt and updatedAt are populated by @PrePersist.
            assignments.add(assignment);
        }

        // Save all assignments through one repository operation.
        List<TeachingAssignment> saved =
                assignmentRepository.saveAll(assignments);

        return saved.stream()
                .map(this::toResponse)
                .toList();
    }

    // GET ALL WITH FILTERS AND PAGINATION
    @Override
    @Transactional(readOnly = true)
    public Page<TeachingAssignmentResponse> getAll(
            Long classId,
            Long subjectId,
            Long staffId,
            AssignmentStatus status,
            int page,
            int size) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100"
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        return assignmentRepository.findFiltered(
                        classId,
                        subjectId,
                        staffId,
                        status,
                        pageable
                )
                .map(this::toResponse);
    }

    // GET ONE ASSIGNMENT
    @Override
    @Transactional(readOnly = true)
    public TeachingAssignmentResponse getById(Long id) {

        TeachingAssignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Teaching assignment not found: " + id
                        ));

        return toResponse(assignment);
    }

    // CHANGE ASSIGNMENT STATUS
    @Override
    @Transactional
    public TeachingAssignmentResponse changeStatus(
            Long id,
            ChangeTeachingAssignmentStatusRequest request) {

        TeachingAssignment assignment =
                assignmentRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Teaching assignment not found: " + id
                        ));

        assignment.setStatus(request.getStatus());

        TeachingAssignment updated =
                assignmentRepository.save(assignment);

        return toResponse(updated);
    }

    // ENTITY TO RESPONSE POJO
    private TeachingAssignmentResponse toResponse(
            TeachingAssignment assignment) {

        return TeachingAssignmentResponse.builder()
                .assignmentId(assignment.getAssignmentId())
                .classId(assignment.getClassId())
                .subjectId(assignment.getSubjectId())
                .staffId(assignment.getStaffId())
                .status(assignment.getStatus())
                .assignedAt(assignment.getAssignedAt())
                .build();
    }
}