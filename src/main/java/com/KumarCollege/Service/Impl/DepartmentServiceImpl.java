
package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.Department;
import com.KumarCollege.Enum.DepartmentStatus;
import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Enum.StudentStatus;
import com.KumarCollege.Pojo.Course.Response.ProgramStudentResponse;
import com.KumarCollege.Pojo.Course.Response.ProgramStudentsResponse;
import com.KumarCollege.Pojo.Department.Req.CreateDepartmentReq;
import com.KumarCollege.Pojo.Department.Req.DepartmentStudentsResponse;
import com.KumarCollege.Pojo.Department.Req.UpdateDepartmentStatus;
import com.KumarCollege.Pojo.Department.Response.DepartmentResponse;
import com.KumarCollege.Repository.DepartmentRepository;
import com.KumarCollege.Service.DepartmentService;
import com.KumarCollege.Validation.DepartmentValidation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentValidation validation;

    @Override
    public List<DepartmentResponse> createBulkDepartments(
            List<CreateDepartmentReq> requests) {

        List<Department> departments = requests.stream()
                .map(request -> {
                    validation.validateDepartmentCodeAndName(
                            request.getDepartmentCode(),
                            request.getDepartmentName()
                    );

                    Department department = new Department();
                    department.setDepartmentCode(
                            request.getDepartmentCode().trim()
                    );
                    department.setDepartmentName(
                            request.getDepartmentName().trim()
                    );
                    department.setHodName(request.getHodName());
                    department.setStatus(DepartmentStatus.ACTIVE);

                    return department;
                })
                .toList();

        return departmentRepository.saveAll(departments)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(int page, int size) {

        validation.validatePage(page, size);

        Pageable pageable = PageRequest.of(page, size);

        return departmentRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long departmentId) {

        Department department = validation.findDepartment(departmentId);

        return toResponse(department);
    }

    @Override
    public DepartmentResponse updateDepartment(
            Long id,
            CreateDepartmentReq request) {

        Department department = validation.findDepartment(id);

        // Ensure the validation checks uniqueness while excluding this ID.
        validation.validateDepartmentCodeAndName(
                request.getDepartmentCode(),
                request.getDepartmentName()
        );

        department.setDepartmentCode(request.getDepartmentCode().trim());
        department.setDepartmentName(request.getDepartmentName().trim());
        department.setHodName(request.getHodName());

        return toResponse(departmentRepository.save(department));
    }

    @Override
    public Department updateDepartmentStatus(
            Long id,
            UpdateDepartmentStatus request) {

        Department department = validation.findDepartment(id);

        if (request.getStatus() == null) {
            throw new IllegalArgumentException(
                    "Department status is required"
            );
        }

        department.setStatus(request.getStatus());

        return departmentRepository.save(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getInActiveDepartment(
            DepartmentStatus status) {

        return departmentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentStudentsResponse getDepartmentStudents(
            Long departmentId,
            StudentStatus studentStatus,
            StudentEnrollmentStatus enrollmentStatus) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found: " + departmentId
                ));

        String studentStatusValue =
                studentStatus == null ? null : studentStatus.name();

        String enrollmentStatusValue =
                enrollmentStatus == null ? null : enrollmentStatus.name();

        List<Object[]> rows = departmentRepository.findDepartmentStudents(
                departmentId,
                studentStatusValue,
                enrollmentStatusValue
        );

        // Keep programs in insertion order.
        Map<Long, ProgramStudentsResponse> programMap =
                new LinkedHashMap<>();

        for (Object[] row : rows) {

            Long programId = ((Number) row[0]).longValue();
            String programName = (String) row[1];

            ProgramStudentsResponse programResponse =
                    programMap.computeIfAbsent(programId, id -> {
                        ProgramStudentsResponse response =
                                new ProgramStudentsResponse();

                        response.setProgramId(id);
                        response.setProgramName(programName);
                        response.setStudents(new ArrayList<>());

                        return response;
                    });

            ProgramStudentResponse student =
                    new ProgramStudentResponse();

            student.setStudentId(((Number) row[2]).longValue());
            student.setStudentName((String) row[3]);
            student.setAdmissionNumber((String) row[4]);
            student.setEmail((String) row[5]);
            student.setSection((String) row[6]);

            student.setStudentStatus(
                    StudentStatus.valueOf(row[7].toString())
            );

            student.setEnrollmentStatus(
                    StudentEnrollmentStatus.valueOf(row[8].toString())
            );

            programResponse.getStudents().add(student);
        }

        DepartmentStudentsResponse response =
                new DepartmentStudentsResponse();

        response.setDepartmentId(department.getDepartmentId());
        response.setDepartmentName(department.getDepartmentName());
        response.setPrograms(new ArrayList<>(programMap.values()));

        return response;
    }

    private DepartmentResponse toResponse(Department department) {

        DepartmentResponse response = new DepartmentResponse();

        response.setDepartmentId(department.getDepartmentId());
        response.setDepartmentCode(department.getDepartmentCode());
        response.setDepartmentName(department.getDepartmentName());
        response.setHodName(department.getHodName());
        response.setStatus(department.getStatus());

        return response;
    }
}
