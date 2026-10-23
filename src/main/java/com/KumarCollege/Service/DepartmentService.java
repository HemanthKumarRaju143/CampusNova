package com.KumarCollege.Service;

import com.KumarCollege.Entity.Department;
import com.KumarCollege.Enum.DepartmentStatus;
import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Enum.StudentStatus;
import com.KumarCollege.Pojo.Department.Req.CreateDepartmentReq;
import com.KumarCollege.Pojo.Department.Req.DepartmentStudentsResponse;
import com.KumarCollege.Pojo.Department.Req.UpdateDepartmentStatus;
import com.KumarCollege.Pojo.Department.Response.DepartmentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DepartmentService {

    public List<DepartmentResponse> createBulkDepartments(List<CreateDepartmentReq> requests);

//    public DepartmentResponse create(CreateDepartmentReq req);

    public Page<DepartmentResponse> getAllDepartments(int page , int size);

    public DepartmentResponse getDepartmentById(Long departmentId);

    public DepartmentResponse updateDepartment(Long id , CreateDepartmentReq req);

    public Department updateDepartmentStatus(Long id , UpdateDepartmentStatus req);

    public List<Department> getInActiveDepartment(DepartmentStatus status);

    DepartmentStudentsResponse getDepartmentStudents(
            Long departmentId,
            StudentStatus studentStatus,
            StudentEnrollmentStatus enrollmentStatus
    );
}
