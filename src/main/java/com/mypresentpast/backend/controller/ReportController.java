package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.request.ReportRequest;
import com.mypresentpast.backend.dto.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reports")
public interface ReportController {
    @PostMapping("/{postId}")
    ResponseEntity<ApiResponse> reportPost(@PathVariable Long postId, @Valid @RequestBody ReportRequest request);
}
