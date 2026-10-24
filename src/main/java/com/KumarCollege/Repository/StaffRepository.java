
package com.KumarCollege.Repository;

import com.KumarCollege.Entity.Staff;
import com.KumarCollege.Enum.StaffStatus;
import com.KumarCollege.Enum.StaffType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends
        JpaRepository<Staff, Long>,
        JpaSpecificationExecutor<Staff> {

    Staff findByEmployeeCodeIgnoreCase(String employeeCode);

    Staff findByEmailIgnoreCase(String email);

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

    boolean existsByEmailIgnoreCase(String email);

    Page<Staff> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Staff> findByStaffType(StaffType staffType, Pageable pageable);

    Page<Staff> findByStatus(StaffStatus status, Pageable pageable);

    boolean existsByEmployeeCodeIgnoreCaseAndStaffIdNot(
            String employeeCode,
            Long staffId
    );

    boolean existsByEmailIgnoreCaseAndStaffIdNot(
            String email,
            Long staffId
    );
}
