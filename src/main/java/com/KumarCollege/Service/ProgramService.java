package com.KumarCollege.Service;

import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Enum.StudentStatus;
import com.KumarCollege.Pojo.Course.Req.CreateProgramReq;
import com.KumarCollege.Pojo.Course.Req.ReplaceProgramRequest;
import com.KumarCollege.Pojo.Course.Response.ProgramResponse;
import com.KumarCollege.Pojo.Course.Response.ProgramStudentsResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProgramService {
    List<ProgramResponse> createProgram(List<CreateProgramReq> requests);

    Page<ProgramResponse> getAllPrograms(int page, int size);

    ProgramResponse getProgramById(Long programId);

    ProgramResponse updateProgram(Long programId, ReplaceProgramRequest request);

    ProgramStudentsResponse getProgramStudents(
            Long programId,
            StudentStatus studentStatus,
            StudentEnrollmentStatus enrollmentStatus
    );

}
