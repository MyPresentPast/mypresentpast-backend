package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.AdminController;
import com.mypresentpast.backend.dto.request.RejectRequestDto;
import com.mypresentpast.backend.dto.response.AdminInstitutionRequestResponse;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.enums.RequestStatus;
import com.mypresentpast.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminControllerImpl implements AdminController {

    private final AdminService adminService;

    @Override
    public ResponseEntity<List<AdminInstitutionRequestResponse>> getAllRequests(RequestStatus status) {
        log.info("Solicitando todas las solicitudes de institución. Filtro estado: {}", status);
        
        List<AdminInstitutionRequestResponse> requests = adminService.getAllRequests(status);
        
        log.info("Se encontraron {} solicitudes", requests.size());
        return ResponseEntity.ok(requests);
    }

    @Override
    public ResponseEntity<AdminInstitutionRequestResponse> getRequestDetail(Long id) {
        log.info("Solicitando detalle de solicitud: {}", id);
        
        AdminInstitutionRequestResponse request = adminService.getRequestDetail(id);
        
        log.info("Detalle de solicitud {} obtenido exitosamente", id);
        return ResponseEntity.ok(request);
    }

    @Override
    public ResponseEntity<ApiResponse> approveRequest(Long id) {
        log.info("Aprobando solicitud: {}", id);
        
        adminService.approveRequest(id);
        
        ApiResponse response = ApiResponse.builder()
                .message("Solicitud aprobada exitosamente. El usuario ahora es una institución.")
                .build();
        
        log.info("Solicitud {} aprobada exitosamente", id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> rejectRequest(Long id, @Valid RejectRequestDto rejectDto) {
        log.info("Rechazando solicitud: {} con motivo: {}", id, rejectDto.getRejectionReason());
        
        adminService.rejectRequest(id, rejectDto.getRejectionReason());
        
        ApiResponse response = ApiResponse.builder()
                .message("Solicitud rechazada exitosamente.")
                .build();
        
        log.info("Solicitud {} rechazada exitosamente", id);
        return ResponseEntity.ok(response);
    }
}
