
package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.Staff;
import com.KumarCollege.Enum.StaffStatus;
import com.KumarCollege.Enum.StaffType;
import com.KumarCollege.Pojo.Staff.Request.ChangeStaffStatusRequest;
import com.KumarCollege.Pojo.Staff.Request.CreateStaffRequest;
import com.KumarCollege.Pojo.Staff.Response.StaffResponse;
import com.KumarCollege.Repository.StaffRepository;
import com.KumarCollege.Service.StaffService;
import com.KumarCollege.Validation.StaffValidation;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffValidation staffValidation;

    // CREATE ONE OR MULTIPLE STAFF MEMBERS
    @Override
    public List<StaffResponse> createStaff(List<CreateStaffRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one staff record is required"
            );
        }

        Set<String> employeeCodes = new HashSet<>();
        Set<String> emails = new HashSet<>();

        // First pass: validate all requests before saving.
        for (CreateStaffRequest request : requests) {

            if (request == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Staff request cannot be null"
                );
            }

            String employeeCode =
                    request.getEmployeeCode().trim().toUpperCase(Locale.ROOT);

            String email =
                    request.getEmail().trim().toLowerCase(Locale.ROOT);

            if (!employeeCodes.add(employeeCode)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate employee code in request: " + employeeCode
                );
            }

            if (!emails.add(email)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate email in request: " + email
                );
            }

            staffValidation.validateDepartmentExists(
                    request.getDepartmentId()
            );

            staffValidation.validateEmployeeCodeAvailable(employeeCode);
            staffValidation.validateEmailAvailable(email);
        }

        // Second pass: create entities after validation succeeds.
        List<Staff> staffList = new ArrayList<>();

        for (CreateStaffRequest request : requests) {
            Staff staff = new Staff();
            staff.setDepartmentId(request.getDepartmentId());

            staff.setEmployeeCode(request.getEmployeeCode().trim().toUpperCase(Locale.ROOT));

            staff.setFirstName(request.getFirstName().trim());

            staff.setLastName(request.getLastName());

            staff.setEmail(request.getEmail());

            staff.setPhone(request.getPhone());

            staff.setStaffType(request.getStaffType());
            staff.setDesignation(request.getDesignation().trim());
            staff.setQualification(request.getQualification());
            staff.setSpecialization(request.getSpecialization());
            staffList.add(staff);
        }

        return staffRepository.saveAll(staffList)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // LIST STAFF WITH OPTIONAL FILTERS AND PAGINATION
    @Override
    @Transactional(readOnly = true)
    public Page<StaffResponse> getAllStaff(
            Long departmentId,
            StaffType staffType,
            StaffStatus status,
            int page,
            int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be >= 0 and size must be between 1 and 100"
            );
        }

        Specification<Staff> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (departmentId != null) {
                predicates.add(
                        cb.equal(root.get("departmentId"), departmentId)
                );
            }

            if (staffType != null) {
                predicates.add(
                        cb.equal(root.get("staffType"), staffType)
                );
            }

            if (status != null) {
                predicates.add(
                        cb.equal(root.get("status"), status)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "staffId")
        );

        return staffRepository.findAll(specification, pageable)
                .map(this::toResponse);
    }

    // GET ONE STAFF MEMBER
    @Override
    @Transactional(readOnly = true)
    public StaffResponse getStaffById(Long id) {
        return toResponse(staffValidation.getStaffById(id));
    }

    // REPLACE STAFF DETAILS
    @Override
    public StaffResponse replaceStaff(
            Long id,
            CreateStaffRequest request
    ) {
        Staff staff = staffValidation.getStaffById(id);

        staffValidation.validateDepartmentExists(
                request.getDepartmentId()
        );

        staffValidation.validateUniqueForUpdate(
                request.getEmployeeCode().trim(),
                request.getEmail().trim(),
                id
        );

        staff.setDepartmentId(request.getDepartmentId());

        staff.setEmployeeCode(request.getEmployeeCode().trim().toUpperCase(Locale.ROOT));

        staff.setFirstName(request.getFirstName().trim());

        staff.setLastName(request.getLastName() == null
                ? null
                : request.getLastName().trim()
        );

        staff.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));

        staff.setPhone(request.getPhone() == null
                ? null
                : request.getPhone().trim()
        );

        staff.setStaffType(request.getStaffType());
        staff.setDesignation(request.getDesignation().trim());
        staff.setQualification(request.getQualification());
        staff.setSpecialization(request.getSpecialization());

        return toResponse(staffRepository.save(staff));
    }

    // CHANGE ONLY STAFF STATUS
    @Override
    public StaffResponse changeStaffStatus(
            Long id,
            ChangeStaffStatusRequest request
    ) {
        Staff staff = staffValidation.getStaffById(id);

        staff.setStatus(request.getStatus());

        return toResponse(staffRepository.save(staff));
    }

    // CONVERT ENTITY INTO RESPONSE DTO
    private StaffResponse toResponse(Staff staff) {
        StaffResponse response = new StaffResponse();

        response.setStaffId(staff.getStaffId());
        response.setDepartmentId(staff.getDepartmentId());
        response.setEmployeeCode(staff.getEmployeeCode());
        response.setFirstName(staff.getFirstName());
        response.setLastName(staff.getLastName());
        response.setEmail(staff.getEmail());
        response.setPhone(staff.getPhone());
        response.setStaffType(staff.getStaffType());
        response.setDesignation(staff.getDesignation());
        response.setQualification(staff.getQualification());
        response.setSpecialization(staff.getSpecialization());
        response.setStatus(staff.getStatus());

        return response;
    }
}
