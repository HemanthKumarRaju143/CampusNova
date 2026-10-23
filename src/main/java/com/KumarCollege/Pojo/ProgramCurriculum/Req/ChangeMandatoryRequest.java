package com.KumarCollege.Pojo.ProgramCurriculum.Req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeMandatoryRequest {

    @NotNull(message = "Mandatory status is required")
    private Boolean isMandatory;
}