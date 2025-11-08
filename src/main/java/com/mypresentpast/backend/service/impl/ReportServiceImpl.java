package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.ApiResponse;
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
import com.mypresentpast.backend.service.ReportService;
import com.mypresentpast.backend.utils.MessageBundle;
import com.mypresentpast.backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PostRepository postRepository;
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

        // Validación de unicidad de negocio (Restricción de duplicidad)
        if (reportRepository.existsActiveReportByPostIdAndReporterId(postId, reporterId, EnumSet.of(ReportStatus.PENDING, ReportStatus.IN_PROGRESS))) {
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
}
