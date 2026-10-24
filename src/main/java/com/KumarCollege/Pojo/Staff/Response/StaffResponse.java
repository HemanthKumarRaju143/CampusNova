package com.KumarCollege.Pojo.Staff.Response;

import com.KumarCollege.Enum.StaffStatus;
import com.KumarCollege.Enum.StaffType;
import lombok.Data;


@Data
public class StaffResponse {
    private Long staffId;
    private Long departmentId;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private StaffType staffType;
    private String designation;
    private String qualification;
    private String specialization;
    private StaffStatus status;
}