package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.response.ReportListingResponse;
import com.mypresentpast.backend.dto.response.ReportDetailResponse;
import com.mypresentpast.backend.enums.ReportStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * Controlador para la administración de reportes de publicaciones.
 * Solo accesible para usuarios con rol ADMIN.
 */
public interface AdminReportController {

    /**
     * Obtiene el listado paginado de reportes de publicaciones.
     * Permite filtrar por estado si se proporciona el parámetro.
     *
     * @param status Estado del reporte (opcional).
     * @param pageable Parámetros de paginación y ordenamiento.
     * @return Listado paginado de reportes.
     */
    @GetMapping
    ResponseEntity<ReportListingResponse> getReports(ReportStatus status, Pageable pageable);

    /**
     * Obtiene el detalle de un reporte específico por su ID.
     *
     * @param reportId ID del reporte.
     * @return Detalle del reporte.
     */
    @GetMapping("/{reportId}")
    ResponseEntity<ReportDetailResponse> getReportDetail(@PathVariable Long reportId);
}
