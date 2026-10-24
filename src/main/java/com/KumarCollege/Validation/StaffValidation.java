
package com.KumarCollege.Validation;

import com.KumarCollege.Entity.Staff;
import com.KumarCollege.Repository.DepartmentRepository;
import com.KumarCollege.Repository.StaffRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class StaffValidation {

    private final StaffRepository staffRepository;
    private final DepartmentRepository departmentRepository;

    public void validateDepartmentExists(Long departmentId) {
        if (departmentId == null || departmentId <= 0
                || !departmentRepository.existsById(departmentId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Department not found: " + departmentId
            );
        }
    }

    public void validateEmployeeCodeAvailable(String employeeCode) {
        if (staffRepository.existsByEmployeeCodeIgnoreCase(employeeCode)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Employee code already exists: " + employeeCode
            );
        }
    }

    public void validateEmailAvailable(String email) {
        if (staffRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists: " + email
            );
        }
    }

    public void validateUniqueForUpdate(
            String employeeCode,
            String email,
            Long staffId
    ) {
        if (staffRepository.existsByEmployeeCodeIgnoreCaseAndStaffIdNot(
                employeeCode, staffId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Employee code already belongs to another staff member"
            );
        }

        if (staffRepository.existsByEmailIgnoreCaseAndStaffIdNot(
                email, staffId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already belongs to another staff member"
            );
        }
    }

    public Staff getStaffById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Staff ID must be positive"
            );
        }

        return staffRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Staff not found with ID: " + id
                ));
    }
}
