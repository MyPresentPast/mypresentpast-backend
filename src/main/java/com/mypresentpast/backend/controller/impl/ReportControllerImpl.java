package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.ReportController;
import com.mypresentpast.backend.dto.request.ReportRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportControllerImpl implements ReportController {

    private final ReportService reportService;

    @Override
    public ResponseEntity<ApiResponse> reportPost(Long postId, ReportRequest request) {
        ApiResponse response = reportService.createReport(postId, request.getReason(), request.getType());
        return ResponseEntity.ok(response);
    }

}
