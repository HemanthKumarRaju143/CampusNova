package com.KumarCollege.Service;

import com.KumarCollege.Pojo.ProgramCurriculum.Req.CreateCurriculumRequest;
import com.KumarCollege.Pojo.ProgramCurriculum.Req.ChangeMandatoryRequest;
import com.KumarCollege.Pojo.ProgramCurriculum.Response.ProgramCurriculumResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProgramCurriculumService {

    List<ProgramCurriculumResponse> create(List<CreateCurriculumRequest> requests);

    Page<ProgramCurriculumResponse> getAll(
            Long programId,
            Long subjectId,
            Byte semester,
            Boolean isMandatory,
            int page,
            int size
    );

    ProgramCurriculumResponse getById(Long id);

    ProgramCurriculumResponse replace(Long id, CreateCurriculumRequest request);

    ProgramCurriculumResponse changeMandatory(
            Long id,
            ChangeMandatoryRequest request
    );
}