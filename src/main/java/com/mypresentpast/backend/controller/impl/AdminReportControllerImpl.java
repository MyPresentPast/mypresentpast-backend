package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.AdminReportController;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.ReportDetailResponse;
import com.mypresentpast.backend.dto.response.ReportListingResponse;
import com.mypresentpast.backend.enums.ReportStatus;
import com.mypresentpast.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')") // Asegura que solo los administradores puedan acceder a estos endpoints
public class AdminReportControllerImpl implements AdminReportController {
    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ReportListingResponse> getReports(
            @RequestParam(required = false) ReportStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(reportService.getReportListing(pageable, status));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDetailResponse> getReportDetail(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReportDetail(reportId));
    }

    @PutMapping("/{reportId}/accept")
    public ResponseEntity<ApiResponse> acceptReport(
            @PathVariable Long reportId,
            @RequestParam(value = "admin_id") Long adminId) {
        return ResponseEntity.ok(reportService.acceptReport(reportId, adminId));
    }

    @PutMapping("/{reportId}/reject")
    public ResponseEntity<ApiResponse> rejectReport(
            @PathVariable Long reportId,
            @RequestParam(value = "admin_id") Long adminId) {
        return ResponseEntity.ok(reportService.rejectReport(reportId, adminId));
    }
}
