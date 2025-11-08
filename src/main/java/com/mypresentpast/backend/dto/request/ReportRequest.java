package com.mypresentpast.backend.dto.request;

import com.mypresentpast.backend.enums.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportRequest {

    @NotBlank
    private String reason;

    @NotNull
    private ReportType type;
}
