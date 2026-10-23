package com.KumarCollege.Controller;

import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Pojo.Course.Response.ProgramStudentResponse;
import com.KumarCollege.Pojo.StudentEnrollment.Request.ChangeEnrollmentStatusRequest;
import com.KumarCollege.Pojo.StudentEnrollment.Request.CreateEnrollmentRequest;
import com.KumarCollege.Pojo.StudentEnrollment.Request.ReplaceEnrollmentRequest;
import com.KumarCollege.Pojo.StudentEnrollment.Response.EnrollmentResponse;
import com.KumarCollege.Service.StudentClassEnrollmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student-class-enrollments")

public class StudentClassEnrollmentController {

    private final StudentClassEnrollmentService enrollmentService;

    // Create multiple enrollments.
    @PostMapping
    public ResponseEntity<List<EnrollmentResponse>> createBulkEnrollments(
            @Valid @RequestBody
            List<@Valid CreateEnrollmentRequest> requests) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.createBulkEnrollments(requests));
    }

    // Get all enrollments, optionally filtered by status.
    @GetMapping
    public ResponseEntity<Page<EnrollmentResponse>> getAllEnrollments(
            @RequestParam(required = false) StudentEnrollmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments(status, page, size)
        );
    }

    // Get enrollment by ID.
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentById(id)
        );
    }

    // Replace enrollment details.
    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> replaceEnrollment(
            @PathVariable Long id,
            @Valid @RequestBody ReplaceEnrollmentRequest request) {

        return ResponseEntity.ok(
                enrollmentService.replaceEnrollment(id, request)
        );
    }

    // Change enrollment status.
    @PatchMapping("/{id}/status")
    public ResponseEntity<EnrollmentResponse> changeEnrollmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeEnrollmentStatusRequest request) {

        return ResponseEntity.ok(
                enrollmentService.changeEnrollmentStatus(id, request)
        );
    }

    // Get enrollments for a student.
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<EnrollmentResponse>> getEnrollmentsByStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) StudentEnrollmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByStudent(
                        studentId, status, page, size
                )
        );
    }

    // Get enrollments for a class.
    @GetMapping("/class/{classId}")
    public ResponseEntity<Page<EnrollmentResponse>> getEnrollmentsByClass(
            @PathVariable Long classId,
            @RequestParam(required = false) StudentEnrollmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByClass(
                        classId, status, page, size
                )
        );
    }

    @GetMapping("/program/{programId}/section/{section}/students") // CHANGED
    public ResponseEntity<List<ProgramStudentResponse>>
    getStudentsByProgramAndSection(
            @PathVariable Long programId, // CHANGED
            @PathVariable String section) {

        return ResponseEntity.ok(
                enrollmentService
                        .getStudentsByProgramAndSection(
                                programId, section // CHANGED
                        )
        );
    }
}