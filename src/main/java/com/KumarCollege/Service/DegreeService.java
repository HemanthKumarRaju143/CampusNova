package com.KumarCollege.Service;

import com.KumarCollege.Enum.DegreeStatus;
import com.KumarCollege.Pojo.Degree.Request.CreateDegreeRequest;
import com.KumarCollege.Pojo.Degree.Response.DegreeResponse;
import com.KumarCollege.Pojo.Degree.Request.UpdateDegreeDurationRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DegreeService {

    List<DegreeResponse> createBulkDegrees(
            List<CreateDegreeRequest> requests
    );

    DegreeResponse getDegreeById(Long id);

    Page<DegreeResponse> getAllDegrees(
            int page,
            int size,
            String sortBy,
            String direction
    );

    DegreeResponse updateDegree(Long id, CreateDegreeRequest request);

    DegreeResponse updateDegreeDuration(
            Long id,
            UpdateDegreeDurationRequest request
    );

    DegreeResponse getDegreeByCode(String code);

    Page<DegreeResponse> getDegreesByLevel(
            DegreeStatus level,
            int page,
            int size
    );

    Page<DegreeResponse> searchDegrees(
            String name,
            int page,
            int size
    );

    boolean degreeCodeExists(String code);
}