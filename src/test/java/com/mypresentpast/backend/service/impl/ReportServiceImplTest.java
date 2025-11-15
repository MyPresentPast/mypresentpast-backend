package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.ReportDetailResponse;
import com.mypresentpast.backend.dto.response.ReportListingResponse;
import com.mypresentpast.backend.enums.ReportStatus;
import com.mypresentpast.backend.enums.ReportType;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.Report;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Report testReport;
    private Post testPost;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setProfileUsername("reporter");
        testUser.setName("Juan");
        testUser.setLastName("Pérez");

        testPost = new Post();
        testPost.setId(2L);
        testPost.setTitle("Título del post");
        testPost.setAuthor(testUser);

        testReport = new Report();
        testReport.setId(10L);
        testReport.setType(ReportType.SPAM);
        testReport.setStatus(ReportStatus.PENDING);
        testReport.setReason("Motivo de prueba");
        testReport.setCreatedAt(LocalDateTime.now());
        testReport.setPost(testPost);
        testReport.setReporter(testUser);

    }

    @Test
    void getReportListing_ReturnsPagedReports() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Report> page = new PageImpl<>(List.of(testReport), pageable, 1);

        when(reportRepository.findAll(pageable)).thenReturn(page);

        ReportListingResponse response = reportService.getReportListing(pageable, null);

        assertNotNull(response);
        assertEquals(1, response.getReports().size());
        assertEquals(testReport.getId(), response.getReports().get(0).getId());
        assertEquals(testReport.getType(), response.getReports().get(0).getType());
        assertEquals(testReport.getStatus(), response.getReports().get(0).getStatus());
        assertEquals(testReport.getPost().getId(), response.getReports().get(0).getPostId());
        assertEquals(testReport.getReporter().getId(), response.getReports().get(0).getReporterId());
    }

    @Test
    void getReportDetail_ReturnsReportDetail() {
        when(reportRepository.findById(10L)).thenReturn(Optional.of(testReport));

        ReportDetailResponse dto = reportService.getReportDetail(10L);

        assertNotNull(dto);
        assertEquals(testReport.getId(), dto.getId());
        assertEquals(testReport.getType(), dto.getType());
        assertEquals(testReport.getStatus(), dto.getStatus());
        assertEquals(testReport.getReason(), dto.getReason());
        assertEquals(testReport.getPost().getId(), dto.getPostId());
        assertEquals(testReport.getPost().getTitle(), dto.getPostName());
        assertEquals(testReport.getReporter().getId(), dto.getReporterId());
        assertEquals(testReport.getReporter().getProfileUsername(), dto.getReporterNickname());
    }

    @Test
    void getReportDetail_ReportNotFound_ThrowsException() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.getReportDetail(99L));
    }

}