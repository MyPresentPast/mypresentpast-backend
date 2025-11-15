package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.ReportListingResponse;
import com.mypresentpast.backend.dto.response.ReportDetailResponse;
import com.mypresentpast.backend.enums.ReportStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

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
    ResponseEntity<ReportListingResponse> getReports(ReportStatus status, Pageable pageable);

    /**
     * Obtiene el detalle de un reporte específico por su ID.
     *
     * @param reportId ID del reporte.
     * @return Detalle del reporte.
     */
    ResponseEntity<ReportDetailResponse> getReportDetail(Long reportId);

    /**
     * Acepta un reporte de publicación, cambiando su estado, eliminando la publicación y registrando el administrador que lo acepta.
     *
     * @param reportId ID del reporte a aceptar.
     * @param adminId ID del administrador que realiza la acción.
     * @return Respuesta indicando el resultado de la operación.
     */
    ResponseEntity<ApiResponse> acceptReport(Long reportId, Long adminId);

    /**
     * Rechaza un reporte de publicación, cambiando su estado, manteniendo la publicación y registrando el administrador que lo rechaza.
     *
     * @param reportId ID del reporte a rechazar.
     * @param adminId ID del administrador que realiza la acción.
     * @return Respuesta indicando el resultado de la operación.
     */
    ResponseEntity<ApiResponse> rejectReport(Long reportId, Long adminId);
}
