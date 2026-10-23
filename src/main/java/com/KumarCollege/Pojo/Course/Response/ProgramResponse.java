package com.KumarCollege.Pojo.Course.Response;

import com.KumarCollege.Enum.ProgramStatus;
import lombok.Data;

@Data
public class ProgramResponse {
    private Long programId; // CHANGED
    private Long departmentId;
    private Long degreeId; // CHANGED: Added
    private String programCode; // CHANGED
    private String programName; // CHANGED
    private ProgramStatus status;
}
