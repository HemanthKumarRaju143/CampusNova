package com.KumarCollege.Validation;

import com.KumarCollege.Entity.Department;
import com.KumarCollege.Enum.DepartmentStatus;
import com.KumarCollege.Repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentValidation {

    private final DepartmentRepository departmentRepository;

    public void validatePage(int page , int size){
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Invalid page number or page size"
            );
        }
    }

    public void validateDepartmentCodeAndName(String code , String name){
        code = code.trim();

        name = name.trim();

        if(departmentRepository.existsByDepartmentNameIgnoreCaseAndDepartmentCodeAndStatus(code , name , DepartmentStatus.ACTIVE)){
            throw new IllegalArgumentException("Department code and name already exists...");
        }
    }

    public Department findDepartment(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Department ID must be positive");
        }

        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found with ID: " + id));
    }


   // public void validate
}
