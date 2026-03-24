package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.ReportDetailResponse;
import com.mypresentpast.backend.dto.response.ReportListingResponse;
import com.mypresentpast.backend.enums.ReportStatus;
import com.mypresentpast.backend.enums.ReportType;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.Post;
import com.mypresentpast.backend.model.Report;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.PostRepository;
import com.mypresentpast.backend.repository.ReportRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.PostService;
import com.mypresentpast.backend.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.EnumSet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostService postService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Report testReport;
    private Post testPost;
    private User testUser;
    private User adminUser;
    private User reporterUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setProfileUsername("reporter");
        testUser.setName("Juan");
        testUser.setLastName("Pérez");

        adminUser = new User();
        adminUser.setId(99L);

        reporterUser = new User();
        reporterUser.setId(3L);
        reporterUser.setProfileUsername("reporter2");

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

    // Simular el comportamiento del repositorio y servicio
    @Test
    void acceptReport_Success() {
        when(reportRepository.findById(10L)).thenReturn(Optional.of(testReport));
        when(userRepository.findById(99L)).thenReturn(Optional.of(adminUser));
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        ApiResponse response = reportService.acceptReport(10L, 99L);

        assertNotNull(response);
        // Verificar que el mensaje contenga la palabra "aceptado"
        assertTrue(response.getMessage().contains("aceptado"));
        // Verificar que el estado del reporte sea ACCEPTED
        assertEquals(ReportStatus.ACCEPTED, testReport.getStatus());
        // Verificar que se haya llamado a deletePost del servicio PostService
        verify(postService).deletePost(anyLong());
    }

    // Simular que el reporte no existe
    @Test
    void acceptReport_ReportNotFound_ThrowsException() {
        when(reportRepository.findById(10L)).thenReturn(Optional.empty());
        // Asegurarse de que se lance la excepción ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> reportService.acceptReport(10L, 99L));
    }

    // Cambiar el estado del reporte a REJECTED para simular un estado inválido
    @Test
    void acceptReport_InvalidStatus_ThrowsException() {
        testReport.setStatus(ReportStatus.REJECTED);
        when(reportRepository.findById(10L)).thenReturn(Optional.of(testReport));
        // Asegurarse de que se lance la excepción BadRequestException
        assertThrows(BadRequestException.class, () -> reportService.acceptReport(10L, 99L));
    }

    // Simular el comportamiento del repositorio y servicio
    @Test
    void rejectReport_Success() {
        when(reportRepository.findById(10L)).thenReturn(Optional.of(testReport));
        when(userRepository.findById(99L)).thenReturn(Optional.of(adminUser));
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        ApiResponse response = reportService.rejectReport(10L, 99L);

        assertNotNull(response);
        // Verificar que el mensaje contenga la palabra "rechazado"
        assertTrue(response.getMessage().contains("rechazado"));
        // Verificar que el estado del reporte sea REJECTED
        assertEquals(ReportStatus.REJECTED, testReport.getStatus());
    }

    // Simular que el reporte no existe
    @Test
    void rejectReport_ReportNotFound_ThrowsException() {
        when(reportRepository.findById(10L)).thenReturn(Optional.empty());
        // asegurarse de que se lance la excepción ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> reportService.rejectReport(10L, 99L));
    }

    // Cambiar el estado del reporte a ACCEPTED para simular un estado inválido
    @Test
    void rejectReport_InvalidStatus_ThrowsException() {
        testReport.setStatus(ReportStatus.ACCEPTED);
        when(reportRepository.findById(10L)).thenReturn(Optional.of(testReport));
        // asegurarse de que se lance la excepción BadRequestException
        assertThrows(BadRequestException.class, () -> reportService.rejectReport(10L, 99L));
    }

    // Verifica que al crear un reporte con datos válidos se retorna el mensaje de éxito y se persiste el reporte.
    @Test
    void createReport_ValidRequest_ReturnsSuccessMessage() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(3L);

            when(userRepository.findById(3L)).thenReturn(Optional.of(reporterUser));
            when(postRepository.findById(2L)).thenReturn(Optional.of(testPost));
            when(reportRepository.existsActiveReportByPostIdAndReporterId(
                    2L, 3L, EnumSet.of(ReportStatus.PENDING, ReportStatus.ACCEPTED)))
                    .thenReturn(false);

            ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
            when(reportRepository.save(reportCaptor.capture())).thenReturn(testReport);

            ApiResponse response = reportService.createReport(2L, "Motivo de prueba", ReportType.SPAM);

            assertNotNull(response);
            assertTrue(response.getMessage().contains("Exito"));

            Report saved = reportCaptor.getValue();
            assertEquals(testPost, saved.getPost());
            assertEquals(reporterUser, saved.getReporter());
            assertEquals("Motivo de prueba", saved.getReason());
            assertEquals(ReportType.SPAM, saved.getType());
            assertEquals(ReportStatus.PENDING, saved.getStatus());

            verify(reportRepository).save(reportCaptor.getValue());
        }
    }

    // Verifica que si el usuario reportero no existe se lanza ResourceNotFoundException.
    @Test
    void createReport_ReporterNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(3L);

            when(userRepository.findById(3L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> reportService.createReport(2L, "Motivo", ReportType.SPAM));

            verify(postRepository, never()).findById(any());
        }
    }

    // Verifica que si la publicación a reportar no existe se lanza ResourceNotFoundException.
    @Test
    void createReport_PostNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(3L);

            when(userRepository.findById(3L)).thenReturn(Optional.of(reporterUser));
            when(postRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> reportService.createReport(99L, "Motivo", ReportType.SPAM));

            verify(reportRepository, never()).existsActiveReportByPostIdAndReporterId(any(), any(), any());
        }
    }

    // Verifica que un usuario no puede reportar su propia publicación.
    @Test
    void createReport_SelfReport_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            // testUser (id=1) es el autor de testPost
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(postRepository.findById(2L)).thenReturn(Optional.of(testPost));

            assertThrows(BadRequestException.class,
                    () -> reportService.createReport(2L, "Motivo", ReportType.SPAM));

            verify(reportRepository, never()).existsActiveReportByPostIdAndReporterId(any(), any(), any());
        }
    }

    // Verifica que no se puede crear un reporte si ya existe uno activo del mismo usuario para la misma publicación.
    @Test
    void createReport_ActiveReportAlreadyExists_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(3L);

            when(userRepository.findById(3L)).thenReturn(Optional.of(reporterUser));
            when(postRepository.findById(2L)).thenReturn(Optional.of(testPost));
            when(reportRepository.existsActiveReportByPostIdAndReporterId(
                    2L, 3L, EnumSet.of(ReportStatus.PENDING, ReportStatus.ACCEPTED)))
                    .thenReturn(true);

            assertThrows(BadRequestException.class,
                    () -> reportService.createReport(2L, "Motivo", ReportType.SPAM));

            verify(reportRepository, never()).save(any());
        }
    }
}