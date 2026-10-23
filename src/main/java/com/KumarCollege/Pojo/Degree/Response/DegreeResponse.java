package com.KumarCollege.Pojo.Degree.Response;

import com.KumarCollege.Enum.DegreeStatus;
import lombok.Data;

@Data
public class DegreeResponse {
    private Long degreeId;
    private String degreeCode;
    private String degreeName;
    private DegreeStatus level;
    private Integer durationYears;
}
