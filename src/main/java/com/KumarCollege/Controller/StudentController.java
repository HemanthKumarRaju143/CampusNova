package com.KumarCollege.Controller;

import com.KumarCollege.Enum.StudentStatus;
import com.KumarCollege.Pojo.Student.Req.ChangeStatusRequest;
import com.KumarCollege.Pojo.Student.Req.CreateStudentRequest;
import com.KumarCollege.Pojo.Student.Req.ReplaceStudentRequest;
import com.KumarCollege.Pojo.Student.Req.TransferStudentRequest;
import com.KumarCollege.Pojo.Student.Response.StudentResponse;
import com.KumarCollege.Service.StudentService;
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
@RequestMapping("/students") // CHANGED: use plural REST resource name
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<List<StudentResponse>> createBulkStudents(
            @RequestBody List<@Valid CreateStudentRequest> requests
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.createStudent(requests));
    }

    @GetMapping
    public ResponseEntity<Page<StudentResponse>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // CHANGED: Optional status lets the client request every status.
            @RequestParam(required = false) StudentStatus status
    ) {
        return ResponseEntity.ok(
                studentService.getAllStudents(status, page, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> replaceRecords(
            @PathVariable Long id,
            @Valid @RequestBody ReplaceStudentRequest request // CHANGED: added @Valid
    ) {
        return ResponseEntity.ok(studentService.replaceRecords(id, request));
    }

    @PatchMapping("/{id}/status") // CHANGED: consistent REST URL
    public ResponseEntity<StudentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request
    ) {
        return ResponseEntity.ok(
                studentService.changeStudentStatus(id, request)
        );
    }

    @PatchMapping("/{id}/transfer") // CHANGED: transfer updates an existing record
    public ResponseEntity<StudentResponse> transferStudent(
            @PathVariable Long id,
            @Valid @RequestBody TransferStudentRequest request
    ) {
        // CHANGED: HTTP 200 OK, not 201 CREATED, because no new student is created.
        return ResponseEntity.ok(
                studentService.transferStudent(id, request)
        );
    }
}