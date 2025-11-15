package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.ReportDetailResponse;
import com.mypresentpast.backend.dto.response.ReportListingResponse;
import com.mypresentpast.backend.enums.ReportStatus;
import com.mypresentpast.backend.enums.ReportType;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    /**
     * Crea un nuevo reporte para una publicación específica por parte de un usuario.
     * * Este método implementa la lógica de negocio para asegurar que:
     * 1. La publicación y el usuario reportador existan.
     * 2. Un mismo usuario no pueda reportar la misma publicación más de una vez (unicidad).
     * 3. El reporte se cree inicialmente con el estado PENDING.
     *
     * @param postId El ID de la publicación que está siendo reportada.
     * @param reason La razón detallada del reporte provista por el usuario.
     * @param type El tipo de incidente reportado (SPAM, ABUSE, etc.).
     * @return El objeto {@code Report} recién creado y persistido.
     * @throws com.mypresentpast.backend.exception.ResourceNotFoundException si el postId o el reporterId no existen.
     * @throws com.mypresentpast.backend.exception.BadRequestException si ya existe un reporte previo de este usuario para la misma publicación.
     */
    ApiResponse createReport(Long postId, String reason, ReportType type);

    /**
     * Obtiene el listado paginado de reportes de publicaciones.
     * Permite filtrar por estado si se proporciona el parámetro.
     *
     * @param pageable Parámetros de paginación y ordenamiento.
     * @param status Estado del reporte (opcional).
     * @return Listado paginado de reportes.
     */
    ReportListingResponse getReportListing(Pageable pageable, ReportStatus status);

    /**
     * Obtiene el detalle de un reporte específico por su ID.
     *
     * @param reportId ID del reporte.
     * @return Detalle del reporte.
     */
    ReportDetailResponse getReportDetail(Long reportId);
}
