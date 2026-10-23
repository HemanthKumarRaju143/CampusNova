
package com.KumarCollege.Service;

import com.KumarCollege.Enum.ClassStatus;
import com.KumarCollege.Pojo.Class.Req.ChangeClassStatusRequest;
import com.KumarCollege.Pojo.Class.Req.CreateClassRequest;
import com.KumarCollege.Pojo.Class.Req.ReplaceRecordRequest;
import com.KumarCollege.Pojo.Class.Response.ClassResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClassService {

    List<ClassResponse> createClass(
            List<CreateClassRequest> requests
    );

    Page<ClassResponse> getAllClasses(
            ClassStatus status,
            int page,
            int size
    );

    ClassResponse getClassById(Long id);

    ClassResponse updateClass(
            Long id,
            ReplaceRecordRequest request
    );

    ClassResponse changeClassStatus(
            Long id,
            ChangeClassStatusRequest request
    );

    // CHANGED: Search classes by programId.
    Page<ClassResponse> getClassesByProgram(
            Long programId,
            ClassStatus status,
            int page,
            int size
    );
}
