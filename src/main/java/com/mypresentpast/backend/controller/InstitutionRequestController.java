package com.mypresentpast.backend.controller;

import com.mypresentpast.backend.dto.request.CreateInstitutionRequestDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.InstitutionRequestResponse;
import com.mypresentpast.backend.service.InstitutionRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controlador para gestionar solicitudes de institución por parte de usuarios.
 */
@RestController
@RequestMapping("/institution-requests")
@RequiredArgsConstructor
public class InstitutionRequestController {

    private final InstitutionRequestService institutionRequestService;

    /**
     * Obtiene todas las solicitudes del usuario actual.
     */
    @GetMapping("/my")
    public ResponseEntity<List<InstitutionRequestResponse>> getMyRequests() {
        List<InstitutionRequestResponse> requests = institutionRequestService.getMyRequests();
        return ResponseEntity.ok(requests);
    }

    /**
     * Crea una nueva solicitud de institución con documento.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createRequest(
            @RequestPart("request") @Valid CreateInstitutionRequestDto request,
            @RequestPart("document") MultipartFile document) {
        ApiResponse response = institutionRequestService.createRequest(request, document);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Cancela una solicitud pendiente.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> cancelRequest(@PathVariable Long id) {
        ApiResponse response = institutionRequestService.cancelRequest(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si el usuario puede crear una nueva solicitud.
     */
    @GetMapping("/can-create")
    public ResponseEntity<Boolean> canCreateNewRequest() {
        boolean canCreate = institutionRequestService.canCreateNewRequest();
        return ResponseEntity.ok(canCreate);
    }
}
