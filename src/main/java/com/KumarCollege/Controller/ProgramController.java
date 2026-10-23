package com.KumarCollege.Controller;

import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Enum.StudentStatus;

import com.KumarCollege.Pojo.Course.Req.CreateProgramReq;
import com.KumarCollege.Pojo.Course.Req.ReplaceProgramRequest;
import com.KumarCollege.Pojo.Course.Response.ProgramResponse;
import com.KumarCollege.Pojo.Course.Response.ProgramStudentsResponse;
import com.KumarCollege.Service.ProgramService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor

// CHANGED: The controller now uses the programs resource.
@RequestMapping("/programs")
public class ProgramController {

    private final ProgramService programService;

    @PostMapping
    public ResponseEntity<List<ProgramResponse>> createProgram(
            @RequestBody List<@Valid CreateProgramReq> requests) {

        List<ProgramResponse> response =
                programService.createProgram(requests);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProgramResponse>> getAllPrograms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                programService.getAllPrograms(page, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramResponse> getProgramById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                programService.getProgramById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramResponse> replaceProgram(
            @PathVariable Long id,
            @Valid @RequestBody ReplaceProgramRequest request) {

        // CHANGED: updateProgram replaces updateCourse.
        return ResponseEntity.ok(
                programService.updateProgram(id, request)
        );
    }

    @GetMapping("/{programId}/students")
    public ResponseEntity<ProgramStudentsResponse> getProgramStudents(
            @PathVariable Long programId,

            @RequestParam(required = false)
            StudentStatus studentStatus,

            @RequestParam(required = false)
            StudentEnrollmentStatus enrollmentStatus) {

        // CHANGED: programId replaces courseId.
        return ResponseEntity.ok(
                programService.getProgramStudents(
                        programId,
                        studentStatus,
                        enrollmentStatus
                )
        );
    }
}