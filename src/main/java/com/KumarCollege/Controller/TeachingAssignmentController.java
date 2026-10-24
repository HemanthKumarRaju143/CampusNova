package com.KumarCollege.Controller;

import com.KumarCollege.Enum.AssignmentStatus;
import com.KumarCollege.Pojo.TeachingAssignment.Request.ChangeTeachingAssignmentStatusRequest;
import com.KumarCollege.Pojo.TeachingAssignment.Request.CreateTeachingAssignmentRequest;
import com.KumarCollege.Pojo.TeachingAssignment.Response.TeachingAssignmentResponse;
import com.KumarCollege.Service.TeachingAssignmentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/teaching-assignments")
public class TeachingAssignmentController {

    private final TeachingAssignmentService assignmentService;

    // BULK CREATE
    @PostMapping
    public ResponseEntity<List<TeachingAssignmentResponse>> createBulk(
            @RequestBody
            List<@Valid CreateTeachingAssignmentRequest> requests) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.createBulk(requests));
    }

    // GET ALL WITH FILTERS AND PAGINATION
    @GetMapping
    public ResponseEntity<Page<TeachingAssignmentResponse>> getAll(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page cannot be negative")
            int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size cannot exceed 100")
            int size) {

        return ResponseEntity.ok(
                assignmentService.getAll(
                        classId, subjectId, staffId, status, page, size
                )
        );
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<TeachingAssignmentResponse> getById(
            @PathVariable @Min(value = 1, message = "ID must be positive")
            Long id) {

        return ResponseEntity.ok(assignmentService.getById(id));
    }

    // CHANGE STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<TeachingAssignmentResponse> changeStatus(
            @PathVariable @Min(value = 1, message = "ID must be positive")
            Long id,
            @Valid @RequestBody
            ChangeTeachingAssignmentStatusRequest request) {

        return ResponseEntity.ok(
                assignmentService.changeStatus(id, request)
        );
    }
}