package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.response.AdminInstitutionRequestResponse;
import com.mypresentpast.backend.enums.RequestStatus;

import java.util.List;

public interface AdminService {
    
    /**
     * Obtiene todas las solicitudes de institución con filtro opcional por estado
     * @param status Estado a filtrar (null = todas)
     * @return Lista de solicitudes con información completa del usuario
     */
    List<AdminInstitutionRequestResponse> getAllRequests(RequestStatus status);
    
    /**
     * Obtiene el detalle completo de una solicitud específica
     * @param requestId ID de la solicitud
     * @return Detalle completo de la solicitud
     */
    AdminInstitutionRequestResponse getRequestDetail(Long requestId);
    
    /**
     * Aprueba una solicitud de institución
     * - Cambia estado a APPROVED
     * - Cambia rol del usuario a INSTITUTION
     * - Registra quién y cuándo aprobó
     * @param requestId ID de la solicitud a aprobar
     */
    void approveRequest(Long requestId);
    
    /**
     * Rechaza una solicitud de institución
     * - Cambia estado a REJECTED
     * - Guarda el motivo del rechazo
     * - Registra quién y cuándo rechazó
     * @param requestId ID de la solicitud a rechazar
     * @param rejectionReason Motivo del rechazo
     */
    void rejectRequest(Long requestId, String rejectionReason);
}
