package com.KumarCollege.Pojo.Staff.Request;

import com.KumarCollege.Enum.StaffStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStaffStatusRequest{

        @NotNull(message = "Staff status is required")
        public StaffStatus status;

}