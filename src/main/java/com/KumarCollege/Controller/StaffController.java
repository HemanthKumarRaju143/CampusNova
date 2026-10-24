package com.KumarCollege.Controller;

import com.KumarCollege.Enum.StaffStatus;
import com.KumarCollege.Enum.StaffType;
import com.KumarCollege.Pojo.Staff.Request.ChangeStaffStatusRequest;
import com.KumarCollege.Pojo.Staff.Request.CreateStaffRequest;
import com.KumarCollege.Pojo.Staff.Response.StaffResponse;
import com.KumarCollege.Service.StaffService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    // 1. Create multiple staff members using dummy JSON data
    @PostMapping
    public ResponseEntity<List<StaffResponse>> createStaff(@RequestBody List<@Valid CreateStaffRequest> requests) {

        List<StaffResponse> response = staffService.createStaff(requests);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 2. Get all staff members with pagination and optional filters
    @GetMapping
    public ResponseEntity<Page<StaffResponse>> getAllStaff(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) StaffType staffType,
            @RequestParam(required = false) StaffStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<StaffResponse> response = staffService.getAllStaff(
                departmentId, staffType, status, page, size
        );

        return ResponseEntity.ok(response);
    }

    // 3. Get one staff member by ID
    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getStaffById(@PathVariable Long id) {

        return ResponseEntity.ok(
                staffService.getStaffById(id)
        );
    }

    // 4. Replace staff details
    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> replaceStaff(
            @PathVariable Long id,
            @Valid @RequestBody CreateStaffRequest request) {

        return ResponseEntity.ok(
                staffService.replaceStaff(id, request)
        );
    }

    // 5. Change staff status
    @PatchMapping("/{id}/status")
    public ResponseEntity<StaffResponse> changeStaffStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStaffStatusRequest request) {

        return ResponseEntity.ok(
                staffService.changeStaffStatus(id, request)
        );
    }
}