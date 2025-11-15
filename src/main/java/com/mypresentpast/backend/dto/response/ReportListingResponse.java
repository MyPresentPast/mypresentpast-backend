package com.mypresentpast.backend.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ReportListingResponse {
    private List<ReportDetailResponse> reports;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
