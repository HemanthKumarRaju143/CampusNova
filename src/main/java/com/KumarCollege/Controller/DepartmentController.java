package com.KumarCollege.Controller;

import com.KumarCollege.Entity.Department;
import com.KumarCollege.Enum.DepartmentStatus;
import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Enum.StudentStatus;
import com.KumarCollege.Pojo.Department.Req.CreateDepartmentReq;
import com.KumarCollege.Pojo.Department.Req.DepartmentStudentsResponse;
import com.KumarCollege.Pojo.Department.Req.UpdateDepartmentStatus;
import com.KumarCollege.Pojo.Department.Response.DepartmentResponse;
import com.KumarCollege.Service.DepartmentService;
import com.KumarCollege.Service.StudentClassEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentService service;

    private final StudentClassEnrollmentService enrollmentService;

    @PostMapping
    public List<DepartmentResponse> createDepartment(@RequestBody List<@Valid CreateDepartmentReq> requests){
        return service.createBulkDepartments(requests);
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        return ResponseEntity.ok(service.getAllDepartments(page,size));
    }

    @GetMapping("byId/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id){
        return ResponseEntity.ok(service.getDepartmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id ,@RequestBody @Valid CreateDepartmentReq req){
        return ResponseEntity.ok(service.updateDepartment(id , req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Department> updateStatus(@PathVariable Long id , @RequestBody UpdateDepartmentStatus req){
        return ResponseEntity.ok(service.updateDepartmentStatus(id , req));
    }

    @GetMapping("/byStatus")
    public ResponseEntity<List<Department>> getDepartmentsByStatus(@RequestParam(defaultValue = "Active")DepartmentStatus status){
        return ResponseEntity.ok(service.getInActiveDepartment(status));
    }

    @GetMapping("/{departmentId}/students")
    public DepartmentStudentsResponse getDepartmentStudents(
            @PathVariable Long departmentId,
            @RequestParam(required = false)
            StudentStatus studentStatus,
            @RequestParam(required = false)
            StudentEnrollmentStatus enrollmentStatus) {

        return service.getDepartmentStudents(
                departmentId,
                studentStatus,
                enrollmentStatus
        );
    }
}
