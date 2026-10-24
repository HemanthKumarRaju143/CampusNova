package com.KumarCollege.Controller;

import com.KumarCollege.Enum.SubjectStatus;
import com.KumarCollege.Pojo.Subjects.Requests.ChangeSubjectStatusRequest;
import com.KumarCollege.Pojo.Subjects.Requests.CreateSubjectRequest;
import com.KumarCollege.Pojo.Subjects.Response.SubjectResponse;
import com.KumarCollege.Service.SubjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    // Create a subject.

    @PostMapping
    public ResponseEntity<List<SubjectResponse>> createBulkSubjects(
            @RequestBody List<@Valid CreateSubjectRequest> requests) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subjectService.createSubjects(requests));
    }

    @GetMapping
    public ResponseEntity<Page<SubjectResponse>> getAllSubjects(
            @RequestParam(required = false) SubjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                subjectService.getAllSubjects(status, page, size)
        );
    }

    // Get a subject by ID.
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                subjectService.getSubjectById(id)
        );
    }

    // Replace subject details.
    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> replaceSubject(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubjectRequest request) {

        return ResponseEntity.ok(
                subjectService.replaceSubject(id, request)
        );
    }

    // Change subject status.
    @PatchMapping("/{id}/status")
    public ResponseEntity<SubjectResponse> changeSubjectStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeSubjectStatusRequest request) {

        return ResponseEntity.ok(
                subjectService.changeSubjectStatus(id, request)
        );
    }
}