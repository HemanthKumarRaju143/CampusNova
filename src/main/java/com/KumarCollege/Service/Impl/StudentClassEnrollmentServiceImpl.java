package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.StudentClassEnrollment;
import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Enum.StudentStatus;
import com.KumarCollege.Pojo.Course.Response.ProgramStudentResponse;
import com.KumarCollege.Pojo.StudentEnrollment.Request.ChangeEnrollmentStatusRequest;
import com.KumarCollege.Pojo.StudentEnrollment.Request.CreateEnrollmentRequest;
import com.KumarCollege.Pojo.StudentEnrollment.Request.ReplaceEnrollmentRequest;
import com.KumarCollege.Pojo.StudentEnrollment.Response.EnrollmentResponse;
import com.KumarCollege.Repository.ProgramRepository;
import com.KumarCollege.Repository.StudentClassEnrollmentRepository;
import com.KumarCollege.Repository.StudentRepository;
import com.KumarCollege.Repository.CollegeClassRepository;

import com.KumarCollege.Service.StudentClassEnrollmentService;
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
public class StudentClassEnrollmentServiceImpl implements StudentClassEnrollmentService {

    private final StudentClassEnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CollegeClassRepository collegeClassRepository;
    private final ProgramRepository programRepository;

    @Override
    @Transactional
    public List<EnrollmentResponse> createBulkEnrollments(
            List<CreateEnrollmentRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one enrollment is required"
            );
        }

        Set<String> requestPairs = new HashSet<>();

        // Validate every request before saving anything.
        for (CreateEnrollmentRequest request : requests) {

            validateStudent(request.getStudentId());
            validateClass(request.getClassId());

            String pair = request.getStudentId()
                    + ":" + request.getClassId();

            if (!requestPairs.add(pair)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate student and class in this request"
                );
            }

            validateUniqueEnrollment(
                    request.getStudentId(),
                    request.getClassId()
            );
        }

        List<StudentClassEnrollment> enrollments =
                new ArrayList<>();

        for (CreateEnrollmentRequest request : requests) {

            StudentClassEnrollment enrollment =
                    new StudentClassEnrollment();

            enrollment.setStudentId(request.getStudentId());
            enrollment.setClassId(request.getClassId());


            enrollments.add(enrollment);
        }

        return enrollmentRepository.saveAll(enrollments)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getAllEnrollments(
            StudentEnrollmentStatus status,
            int page,
            int size) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<StudentClassEnrollment> enrollments;

        if (status == null) {
            enrollments = enrollmentRepository.findAll(pageable);
        } else {
            enrollments = enrollmentRepository.findByStatus(
                    status, pageable
            );
        }

        return enrollments.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long id) {

        return convertToResponse(findEnrollmentOrThrow(id));
    }

    @Override
    @Transactional
    public EnrollmentResponse replaceEnrollment(
            Long id,
            ReplaceEnrollmentRequest request) {

        StudentClassEnrollment enrollment =
                findEnrollmentOrThrow(id);

        validateStudent(request.getStudentId());
        validateClass(request.getClassId());

        if (enrollmentRepository
                .existsByStudentIdAndClassIdAndEnrollmentIdNot(
                        request.getStudentId(),
                        request.getClassId(),
                        id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This student is already enrolled in this class"
            );
        }

        enrollment.setStudentId(request.getStudentId());
        enrollment.setClassId(request.getClassId());

        return convertToResponse(
                enrollmentRepository.save(enrollment)
        );
    }

    @Override
    public EnrollmentResponse changeEnrollmentStatus(
            Long id,
            ChangeEnrollmentStatusRequest request) {

        StudentClassEnrollment enrollment =
                findEnrollmentOrThrow(id);

        enrollment.setStatus(request.getStatus());

        return convertToResponse(
                enrollmentRepository.save(enrollment)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getEnrollmentsByStudent(
            Long studentId,
            StudentEnrollmentStatus status,
            int page,
            int size) {

        validatePagination(page, size);
        validateStudent(studentId);

        Pageable pageable = PageRequest.of(page, size);

        Page<StudentClassEnrollment> enrollments;

        if (status == null) {
            enrollments = enrollmentRepository.findByStudentId(
                    studentId, pageable
            );
        } else {
            enrollments = enrollmentRepository
                    .findByStudentIdAndStatus(
                            studentId, status, pageable
                    );
        }

        return enrollments.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getEnrollmentsByClass(
            Long classId,
            StudentEnrollmentStatus status,
            int page,
            int size) {

        validatePagination(page, size);
        validateClass(classId);

        Pageable pageable = PageRequest.of(page, size);

        Page<StudentClassEnrollment> enrollments;

        if (status == null) {
            enrollments = enrollmentRepository.findByClassId(
                    classId, pageable
            );
        } else {
            enrollments = enrollmentRepository.findByClassIdAndStatus(
                    classId, status, pageable
            );
        }

        return enrollments.map(this::convertToResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ProgramStudentResponse> getStudentsByProgramAndSection(
            Long programId, // CHANGED: courseId -> programId
            String section) {

        // CHANGED: Call the new program-based repository query.
        List<Object[]> rows =
                enrollmentRepository.findStudentsByProgramAndSection(
                        programId, section
                );

        List<ProgramStudentResponse> students = new ArrayList<>();

        for (Object[] row : rows) {
            ProgramStudentResponse student = new ProgramStudentResponse();

            // Existing response mapping retained.
            student.setStudentId(((Number) row[2]).longValue());
            student.setStudentName((String) row[3]);
            student.setAdmissionNumber((String) row[4]);
            student.setEmail((String) row[5]);
            student.setSection((String) row[6]);

            student.setStudentStatus(
                    StudentStatus.valueOf(row[7].toString())
            );

            student.setEnrollmentStatus(
                    StudentEnrollmentStatus.valueOf(row[8].toString())
            );

            students.add(student);
        }

        return students;
    }



    private StudentClassEnrollment findEnrollmentOrThrow(Long id) {

        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Enrollment not found with ID: " + id
                ));
    }

    private void validateStudent(Long studentId) {

        if (studentId == null ||
                !studentRepository.existsById(studentId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Student not found with ID: " + studentId
            );
        }
    }

    private void validateClass(Long classId) {

        if (classId == null ||
                !collegeClassRepository.existsById(classId)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Class not found with ID: " + classId
            );
        }
    }

    private void validateUniqueEnrollment(
            Long studentId,
            Long classId) {

        if (enrollmentRepository
                .existsByStudentIdAndClassId(studentId, classId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student " + studentId
                            + " is already enrolled in class " + classId
            );
        }
    }

    private void validatePagination(int page, int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be >= 0 and size must be between 1 and 100"
            );
        }
    }

    private EnrollmentResponse convertToResponse(StudentClassEnrollment enrollment) {

        EnrollmentResponse response = new EnrollmentResponse();

        response.setEnrollmentId(enrollment.getEnrollmentId());
        response.setStudentId(enrollment.getStudentId());
        response.setClassId(enrollment.getClassId());
        response.setEnrollmentDate(enrollment.getEnrollmentDate());
        response.setStatus(enrollment.getStatus());

        return response;
    }
}