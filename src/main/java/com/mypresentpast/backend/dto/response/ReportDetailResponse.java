package com.mypresentpast.backend.dto.response;

import com.mypresentpast.backend.enums.ReportStatus;
import com.mypresentpast.backend.enums.ReportType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportDetailResponse {
    private Long id;
    private ReportType type;
    private ReportStatus status;
    private String reason;
    private LocalDateTime createdAt;
    private Long postId;
    private String postName;
    private Long reporterId;
    private String reporterNickname;
    private String postAuthorNickname;
}
