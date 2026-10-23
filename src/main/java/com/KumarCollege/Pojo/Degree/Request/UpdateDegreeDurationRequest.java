package com.KumarCollege.Pojo.Degree.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDegreeDurationRequest{

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 year")
    @Max(value = 15, message = "Duration cannot exceed 15 years")
    private Integer durationYears;
}
