
package com.KumarCollege.Pojo.Class.Response;

import com.KumarCollege.Enum.ClassStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassResponse {

    private Long classId;

    private Long programId;

    private String className;
    private String academicYear;

    private Byte semester;

    private String section;
    private ClassStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
