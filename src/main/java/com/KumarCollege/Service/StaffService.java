package com.KumarCollege.Service;

import com.KumarCollege.Enum.StaffStatus;
import com.KumarCollege.Enum.StaffType;
import com.KumarCollege.Pojo.Staff.Request.ChangeStaffStatusRequest;
import com.KumarCollege.Pojo.Staff.Request.CreateStaffRequest;
import com.KumarCollege.Pojo.Staff.Response.StaffResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StaffService {

    List<StaffResponse> createStaff(List<CreateStaffRequest> requests);

    Page<StaffResponse> getAllStaff(
            Long departmentId,
            StaffType staffType,
            StaffStatus status,
            int page,
            int size
    );

    StaffResponse getStaffById(Long id);

    StaffResponse replaceStaff(Long id, CreateStaffRequest request);

    StaffResponse changeStaffStatus(
            Long id,
            ChangeStaffStatusRequest request
    );
}