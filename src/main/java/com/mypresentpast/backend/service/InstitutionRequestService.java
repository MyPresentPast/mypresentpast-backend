package com.mypresentpast.backend.service;

import com.mypresentpast.backend.dto.request.CreateInstitutionRequestDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.InstitutionRequestResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Servicio para gestionar solicitudes de institución.
 */
public interface InstitutionRequestService {

    /**
     * Obtiene todas las solicitudes del usuario actual.
     */
    List<InstitutionRequestResponse> getMyRequests();

    /**
     * Crea una nueva solicitud de institución con documento.
     */
    ApiResponse createRequest(CreateInstitutionRequestDto request, MultipartFile document);

    /**
     * Cancela una solicitud pendiente.
     */
    ApiResponse cancelRequest(Long requestId);

    /**
     * Verifica si el usuario actual puede crear una nueva solicitud.
     */
    boolean canCreateNewRequest();
}
