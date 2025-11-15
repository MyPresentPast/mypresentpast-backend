package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.*;
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
import com.mypresentpast.backend.service.ReportService;
import com.mypresentpast.backend.utils.MessageBundle;
import com.mypresentpast.backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PostRepository postRepository;
    private final PostService postService;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public ApiResponse createReport(Long postId, String reason, ReportType type) {

        Long reporterId = SecurityUtils.getCurrentUserId();

        // Validación y obtención del Usuario (Reportero)
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(MessageBundle.USER_NOT_FOUND_WITH_ID, reporterId)));

        // Validación y obtención de la Publicación (Post)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(MessageBundle.POST_NOT_FOUND_WITH_ID, postId, reporterId)));

        // Validación para evitar que el usuario reporte su propia publicación
        if (post.getAuthor().getId().equals(reporterId)) {
            throw new BadRequestException(String.format(MessageBundle.REPORT_SELF_POST_NOT_ALLOWED, reporterId, postId));
        }

        // Si ya existe un reporte activo (PENDING o ACCEPTED) por parte del mismo usuario para la misma publicación, lanzar excepción
        if (reportRepository.existsActiveReportByPostIdAndReporterId(postId, reporterId, EnumSet.of(ReportStatus.PENDING, ReportStatus.ACCEPTED))) {
            throw new BadRequestException(String.format(MessageBundle.REPORT_ALREADY_EXISTS_WITH_IDS, reporterId, postId));
        }
        // Construcción del Reporte
        Report report = Report.builder()
                .post(post)
                .reporter(reporter)
                .reason(reason)
                .type(type)
                // Asigna el estado inicial al reporte. Por defecto, cada reporte nuevo está Pendiente de revisión.
                .status(ReportStatus.PENDING)
                .build();

        reportRepository.save(report);
        return ApiResponse
                .builder()
                .message("Publicación reportada con Exito.")
                .build();

    }

    @Override
    public ReportListingResponse getReportListing(Pageable pageable, ReportStatus status) {
        Page<Report> page;
        // Si se proporciona un estado, filtrar por ese estado
        if (status != null) {
            page = reportRepository.findByStatus(status, pageable);
        } else {
            page = reportRepository.findAll(pageable);
        }
        // Mapear entidades a DTOs
        List<ReportDetailResponse> dtos = page.getContent().stream()
                .map(this::mapToReportDetailResponse)
                .toList();
        // Construir la respuesta de paginación
        ReportListingResponse response = new ReportListingResponse();
        response.setReports(dtos);
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());

        return response;
    }

    @Override
    public ReportDetailResponse getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(MessageBundle.REPORT_NOT_FOUND_WITH_ID, reportId)));
        return mapToDetailResponse(report);
    }

    // Métodos de mapeo
    private ReportDetailResponse mapToReportDetailResponse(Report report) {
        ReportDetailResponse dto = new ReportDetailResponse();
        dto.setId(report.getId());
        dto.setType(report.getType());
        dto.setStatus(report.getStatus());
        dto.setReason(report.getReason());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setPostId(report.getPost().getId());
        dto.setReporterId(report.getReporter().getId());
        dto.setPostName(report.getPost().getTitle());
        return dto;
    }

    private ReportDetailResponse mapToDetailResponse(Report report) {
        ReportDetailResponse dto = new ReportDetailResponse();
        dto.setId(report.getId());
        dto.setType(report.getType());
        dto.setStatus(report.getStatus());
        dto.setReason(report.getReason());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setPostId(report.getPost().getId());
        dto.setPostName(report.getPost().getTitle());
        dto.setReporterId(report.getReporter().getId());
        dto.setReporterNickname(report.getReporter().getProfileUsername());
        dto.setPostAuthorNickname(report.getPost().getAuthor().getProfileUsername());
        return dto;
    }

    @Override
    @Transactional
    public ApiResponse acceptReport(Long reportId, Long adminId) {
        // Obtener el reporte
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ResourceNotFoundException(String.format(MessageBundle.REPORT_NOT_FOUND_WITH_ID, reportId)));

        // Verificar que el reporte esté en estado PENDING
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new BadRequestException(String.format(MessageBundle.REPORT_ALREADY_PROCESSED, reportId));
        }
        report.setStatus(ReportStatus.ACCEPTED);
        report.setDecisionAt(LocalDateTime.now());

        // Obtener el administrador que toma la decisión
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format(MessageBundle.ADMIN_NOT_FOUND_WITH_ID, adminId)));

        report.setDecidedBy(admin);

        // Guardar los cambios en el reporte y eliminar la publicación (eliminación lógica)
        reportRepository.save(report);
        postService.deletePost(report.getPost().getId());

        String msg = String.format(MessageBundle.REPORT_ACCEPTED_AND_POST_DELETED, report.getId(), admin.getId(), report.getPost().getId());

        return ApiResponse.builder().message(msg).build();
    }

    @Override
    @Transactional
    public ApiResponse rejectReport(Long reportId, Long adminId) {
        // Obtener el reporte
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ResourceNotFoundException(String.format(MessageBundle.REPORT_NOT_FOUND_WITH_ID, reportId)));

        // Verificar que el reporte esté en estado PENDING
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new BadRequestException(String.format(MessageBundle.REPORT_ALREADY_PROCESSED, reportId));
        }
        report.setStatus(ReportStatus.REJECTED);
        report.setDecisionAt(LocalDateTime.now());

        // Obtener el administrador que toma la decisión
        User admin = userRepository.findById(adminId).orElseThrow(() -> new ResourceNotFoundException(String.format(MessageBundle.ADMIN_NOT_FOUND_WITH_ID, adminId)));
        report.setDecidedBy(admin);

        // Guardar los cambios en el reporte
        reportRepository.save(report);

        String msg = String.format(MessageBundle.REPORT_REJECTED_AND_ARCHIVED, report.getId(), admin.getId(), report.getPost().getId());
        return ApiResponse.builder().message(msg).build();
    }
}
