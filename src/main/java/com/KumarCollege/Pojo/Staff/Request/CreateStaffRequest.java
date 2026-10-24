package com.KumarCollege.Pojo.Staff.Request;

import com.KumarCollege.Enum.StaffType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateStaffRequest{

        @NotNull(message = "Department ID is required")
        @Positive(message = "Department ID must be positive")
        private Long departmentId;

        @NotBlank(message = "Employee code is required")
        @Size(max = 30, message = "Employee code cannot exceed 30 characters")
        private String employeeCode;

        @NotBlank(message = "First name is required")
        @Size(max = 60, message = "First name cannot exceed 60 characters")
        private String firstName;

        @Size(max = 60, message = "Last name cannot exceed 60 characters")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 150, message = "Email cannot exceed 150 characters")
        private String email;

        @Size(max = 20, message = "Phone cannot exceed 20 characters")
        private String phone;

        @NotNull(message = "Staff type is required")
        private StaffType staffType;

        @NotBlank(message = "Designation is required")
        @Size(max = 80, message = "Designation cannot exceed 80 characters")
        private String designation;

        @Size(max = 100, message = "Qualification cannot exceed 100 characters")
        private String qualification;

        @Size(max = 120, message = "Specialization cannot exceed 120 characters")
        private String specialization;
 }