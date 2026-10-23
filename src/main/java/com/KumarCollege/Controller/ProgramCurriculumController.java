package com.KumarCollege.Controller;

import com.KumarCollege.Pojo.ProgramCurriculum.Req.ChangeMandatoryRequest;
import com.KumarCollege.Pojo.ProgramCurriculum.Req.CreateCurriculumRequest;
import com.KumarCollege.Pojo.ProgramCurriculum.Response.ProgramCurriculumResponse;
import com.KumarCollege.Service.ProgramCurriculumService;
import jakarta.validation.Valid;
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
@RequestMapping("/program-curriculum")
public class ProgramCurriculumController {

    private final ProgramCurriculumService curriculumService;

    // CREATE: one or multiple records using one method.
    @PostMapping
    public ResponseEntity<List<ProgramCurriculumResponse>> create(
            @Valid @RequestBody List<@Valid CreateCurriculumRequest> requests) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(curriculumService.create(requests));
    }

    // READ: all records, with optional filters and pagination.
    @GetMapping
    public ResponseEntity<Page<ProgramCurriculumResponse>> getAll(
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Byte semester,
            @RequestParam(required = false) Boolean isMandatory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                curriculumService.getAll(
                        programId, subjectId, semester,
                        isMandatory, page, size
                )
        );
    }

    // READ: one record.
    @GetMapping("/{id}")
    public ResponseEntity<ProgramCurriculumResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(curriculumService.getById(id));
    }

    // UPDATE: replace editable fields.
    @PutMapping("/{id}")
    public ResponseEntity<ProgramCurriculumResponse> replace(
            @PathVariable Long id,
            @Valid @RequestBody CreateCurriculumRequest request) {

        return ResponseEntity.ok(
                curriculumService.replace(id, request)
        );
    }

    // PARTIAL UPDATE: change only mandatory/elective status.
    @PatchMapping("/{id}/mandatory")
    public ResponseEntity<ProgramCurriculumResponse> changeMandatory(
            @PathVariable Long id,
            @Valid @RequestBody ChangeMandatoryRequest request) {

        return ResponseEntity.ok(
                curriculumService.changeMandatory(id, request)
        );
    }
}