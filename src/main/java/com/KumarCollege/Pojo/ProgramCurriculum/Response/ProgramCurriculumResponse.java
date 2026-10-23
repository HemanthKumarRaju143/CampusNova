package com.KumarCollege.Pojo.ProgramCurriculum.Response;

import lombok.Data;

@Data
public class ProgramCurriculumResponse {
    private Long curriculumId;
    private Long programId;
    private Long subjectId;
    private Byte semester;
    private Boolean isMandatory;
}
