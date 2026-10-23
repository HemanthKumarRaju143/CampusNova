
package com.KumarCollege.Controller;

import com.KumarCollege.Enum.ClassStatus;
import com.KumarCollege.Pojo.Class.Req.ChangeClassStatusRequest;
import com.KumarCollege.Pojo.Class.Req.CreateClassRequest;
import com.KumarCollege.Pojo.Class.Req.ReplaceRecordRequest;
import com.KumarCollege.Pojo.Class.Response.ClassResponse;
import com.KumarCollege.Service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/classes")
public class ClassController {

    private final ClassService collegeClassService;

    @PostMapping
    public ResponseEntity<List<ClassResponse>> createBulkClasses(
            @RequestBody List<@Valid CreateClassRequest> requests) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collegeClassService.createClass(requests));
    }

    @GetMapping
    public ResponseEntity<Page<ClassResponse>> getAllClasses(
            @RequestParam(required = false) ClassStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                collegeClassService.getAllClasses(status, page, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassResponse> getClassById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                collegeClassService.getClassById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassResponse> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody ReplaceRecordRequest request) {

        return ResponseEntity.ok(
                collegeClassService.updateClass(id, request)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClassResponse> changeClassStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeClassStatusRequest request) {

        return ResponseEntity.ok(
                collegeClassService.changeClassStatus(id, request)
        );
    }

    // CHANGED: /course/{courseId} -> /program/{programId}
    @GetMapping("/program/{programId}")
    public ResponseEntity<Page<ClassResponse>> getClassesByProgram(
            @PathVariable Long programId,
            @RequestParam(required = false) ClassStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                collegeClassService.getClassesByProgram(
                        programId, status, page, size
                )
        );
    }
}
