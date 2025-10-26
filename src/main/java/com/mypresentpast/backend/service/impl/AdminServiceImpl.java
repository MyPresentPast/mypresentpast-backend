package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.dto.response.AdminInstitutionRequestResponse;
import com.mypresentpast.backend.enums.RequestStatus;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.exception.UnauthorizedException;
import com.mypresentpast.backend.model.InstitutionRequest;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.repository.InstitutionRequestRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.AdminService;
import com.mypresentpast.backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final InstitutionRequestRepository institutionRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminInstitutionRequestResponse> getAllRequests(RequestStatus status) {
        log.info("Admin {} solicitando todas las solicitudes con estado: {}", 
                SecurityUtils.getCurrentUserId(), status);

        List<InstitutionRequest> requests;
        
        if (status != null) {
            requests = institutionRequestRepository.findByStatusWithUserInfo(status);
        } else {
            requests = institutionRequestRepository.findAllWithUserInfo();
        }

        return requests.stream()
                .map(this::mapToAdminResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminInstitutionRequestResponse getRequestDetail(Long requestId) {
        log.info("Admin {} solicitando detalle de solicitud: {}", 
                SecurityUtils.getCurrentUserId(), requestId);

        InstitutionRequest request = institutionRequestRepository.findByIdWithUserInfo(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + requestId));

        return mapToAdminResponse(request);
    }

    @Override
    @Transactional
    public void approveRequest(Long requestId) {
        Long currentAdminId = SecurityUtils.getCurrentUserId();
        log.info("Admin {} aprobando solicitud: {}", currentAdminId, requestId);

        // Buscar la solicitud
        InstitutionRequest request = institutionRequestRepository.findByIdWithUserInfo(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + requestId));

        // Validaciones
        validateRequestCanBeProcessed(request);
        validateUserCanBecomeInstitution(request.getUser());

        // Obtener admin actual
        User currentAdmin = userRepository.findById(currentAdminId)
                .orElseThrow(() -> new UnauthorizedException("Admin no encontrado"));

        // Cambiar estado de la solicitud
        request.setStatus(RequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(currentAdmin);
        request.setRejectionReason(null); // Limpiar cualquier motivo previo

        // Cambiar rol del usuario a INSTITUTION
        User user = request.getUser();
        user.setRole(UserRole.INSTITUTION);
        
        // Guardar cambios
        institutionRequestRepository.save(request);
        userRepository.save(user);

        log.info("Solicitud {} aprobada exitosamente. Usuario {} ahora es INSTITUTION", 
                requestId, user.getId());
    }

    @Override
    @Transactional
    public void rejectRequest(Long requestId, String rejectionReason) {
        Long currentAdminId = SecurityUtils.getCurrentUserId();
        log.info("Admin {} rechazando solicitud: {} con motivo: {}", 
                currentAdminId, requestId, rejectionReason);

        // Validar motivo
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            throw new BadRequestException("El motivo de rechazo es obligatorio");
        }

        if (rejectionReason.length() < 10 || rejectionReason.length() > 500) {
            throw new BadRequestException("El motivo debe tener entre 10 y 500 caracteres");
        }

        // Buscar la solicitud
        InstitutionRequest request = institutionRequestRepository.findByIdWithUserInfo(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + requestId));

        // Validaciones
        validateRequestCanBeProcessed(request);

        // Obtener admin actual
        User currentAdmin = userRepository.findById(currentAdminId)
                .orElseThrow(() -> new UnauthorizedException("Admin no encontrado"));

        // Cambiar estado de la solicitud
        request.setStatus(RequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(currentAdmin);
        request.setRejectionReason(rejectionReason.trim());

        // Guardar cambios
        institutionRequestRepository.save(request);

        log.info("Solicitud {} rechazada exitosamente con motivo: {}", requestId, rejectionReason);
    }

    /**
     * Valida que la solicitud pueda ser procesada (aprobada o rechazada).
     */
    private void validateRequestCanBeProcessed(InstitutionRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Solo se pueden procesar solicitudes en estado PENDING. " +
                    "Estado actual: " + request.getStatus());
        }
    }

    /**
     * Valida que el usuario pueda convertirse en institución.
     */
    private void validateUserCanBecomeInstitution(User user) {
        if (user.getRole() == UserRole.INSTITUTION) {
            throw new BadRequestException("El usuario ya es una institución");
        }

        if (user.getRole() == UserRole.ADMIN) {
            throw new BadRequestException("No se puede convertir un admin en institución");
        }
    }

    /**
     * Mapea una InstitutionRequest a AdminInstitutionRequestResponse.
     */
    private AdminInstitutionRequestResponse mapToAdminResponse(InstitutionRequest request) {
        return AdminInstitutionRequestResponse.builder()
                .id(request.getId())
                .type(request.getType())
                .status(request.getStatus())
                .user(mapUserToDto(request.getUser()))
                .institutionName(request.getInstitutionName())
                .legalAddress(request.getLegalAddress())
                .documentUrl(request.getDocumentUrl())
                .officialPhone(request.getOfficialPhone())
                .officialRegistryNumber(request.getOfficialRegistryNumber())
                .officialWebsite(request.getOfficialWebsite())
                .rejectionReason(request.getRejectionReason())
                .createdAt(request.getCreatedAt())
                .reviewedAt(request.getReviewedAt())
                .reviewedBy(request.getReviewedBy() != null ? mapUserToDto(request.getReviewedBy()) : null)
                .build();
    }

    /**
     * Mapea un User a UserDto.
     */
    private UserDto mapUserToDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .type(user.getRole())
                .avatar(user.getAvatar())
                .build();
    }
}
