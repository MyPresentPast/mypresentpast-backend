package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.request.RejectRequestDto;
import com.mypresentpast.backend.dto.response.AdminInstitutionRequestResponse;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.enums.RequestStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador para operaciones administrativas.
 * Solo accesible por usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/admin/institution-requests")
public interface AdminController {

    /**
     * Obtiene todas las solicitudes de institución.
     * @param status Filtro opcional por estado
     * @return Lista de solicitudes con información completa
     */
    @GetMapping
    ResponseEntity<List<AdminInstitutionRequestResponse>> getAllRequests(
            @RequestParam(required = false) RequestStatus status);

    /**
     * Obtiene el detalle completo de una solicitud específica.
     * @param id ID de la solicitud
     * @return Detalle completo de la solicitud
     */
    @GetMapping("/{id}")
    ResponseEntity<AdminInstitutionRequestResponse> getRequestDetail(@PathVariable Long id);

    /**
     * Aprueba una solicitud de institución.
     * - Cambia el estado a APPROVED
     * - Convierte al usuario en INSTITUTION
     * @param id ID de la solicitud a aprobar
     * @return Respuesta de éxito
     */
    @PutMapping("/{id}/approve")
    ResponseEntity<ApiResponse> approveRequest(@PathVariable Long id);

    /**
     * Rechaza una solicitud de institución.
     * - Cambia el estado a REJECTED
     * - Guarda el motivo del rechazo
     * @param id ID de la solicitud a rechazar
     * @param rejectDto DTO con el motivo del rechazo
     * @return Respuesta de éxito
     */
    @PutMapping("/{id}/reject")
    ResponseEntity<ApiResponse> rejectRequest(
            @PathVariable Long id,
            @Valid @RequestBody RejectRequestDto rejectDto);
}
