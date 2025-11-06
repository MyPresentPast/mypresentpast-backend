package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.dto.request.CreateInstitutionRequestDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.InstitutionRequestResponse;
import com.mypresentpast.backend.enums.RequestStatus;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.InstitutionRequest;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.InstitutionRequestRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import com.mypresentpast.backend.service.InstitutionRequestService;
import com.mypresentpast.backend.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para gestionar solicitudes de institución.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InstitutionRequestServiceImpl implements InstitutionRequestService {

    private final InstitutionRequestRepository institutionRequestRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    // Tipos de archivo permitidos para documentos institucionales
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "application/pdf",
        "image/jpeg", 
        "image/jpg",
        "image/png"
    );

    // Tamaño máximo: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB en bytes

    @Override
    @Transactional(readOnly = true)
    public List<InstitutionRequestResponse> getMyRequests() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Obteniendo solicitudes de institución para usuario: {}", currentUserId);

        List<InstitutionRequest> requests = institutionRequestRepository
            .findByUserIdOrderByCreatedAtDesc(currentUserId);

        return requests.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public ApiResponse createRequest(CreateInstitutionRequestDto request, MultipartFile document) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Creando solicitud de institución para usuario: {}", currentUserId);

        // 1. Validar que el usuario existe y es NORMAL
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getRole() != UserRole.NORMAL) {
            throw new BadRequestException("Solo los usuarios normales pueden solicitar ser institución");
        }

        // 2. Validar que no tenga una solicitud activa
        if (institutionRequestRepository.hasActiveRequest(currentUserId)) {
            throw new BadRequestException("Ya tienes una solicitud activa. No puedes crear otra hasta que la actual sea procesada");
        }

        // 3. Validar y subir el documento
        validateDocument(document);
        String documentUrl = uploadDocument(document, currentUserId);

        // 4. Crear la nueva solicitud
        InstitutionRequest institutionRequest = InstitutionRequest.builder()
            .user(user)
            .institutionName(request.getInstitutionName())
            .legalAddress(request.getLegalAddress())
            .documentUrl(documentUrl)
            .officialPhone(request.getOfficialPhone())
            .type(request.getType())
            .officialRegistryNumber(request.getOfficialRegistryNumber())
            .officialWebsite(request.getOfficialWebsite())
            .status(RequestStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

        institutionRequestRepository.save(institutionRequest);

        log.info("Solicitud de institución creada exitosamente para usuario: {} con ID: {}", 
                 currentUserId, institutionRequest.getId());

        return ApiResponse.builder()
            .message("Solicitud de institución creada exitosamente. Será revisada por nuestro equipo")
            .build();
    }

    @Override
    public ApiResponse cancelRequest(Long requestId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Cancelando solicitud de institución: {} por usuario: {}", requestId, currentUserId);

        // 1. Buscar la solicitud y verificar ownership
        InstitutionRequest request = institutionRequestRepository
            .findByIdAndUserId(requestId, currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        // 2. Validar que se puede cancelar (solo PENDING)
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Solo se pueden cancelar solicitudes pendientes. " +
                                        "Estado actual: " + request.getStatus());
        }

        // 3. Actualizar estado a CANCELLED
        request.setStatus(RequestStatus.CANCELLED);
        request.setReviewedAt(LocalDateTime.now());
        institutionRequestRepository.save(request);

        log.info("Solicitud de institución cancelada exitosamente: {}", requestId);

        return ApiResponse.builder()
            .message("Solicitud cancelada exitosamente")
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCreateNewRequest() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // Verificar que el usuario es NORMAL
        User user = userRepository.findById(currentUserId)
            .orElse(null);
        
        if (user == null || user.getRole() != UserRole.NORMAL) {
            return false;
        }

        // Verificar que no tiene solicitud activa
        return !institutionRequestRepository.hasActiveRequest(currentUserId);
    }

    /**
     * Mapea una entidad InstitutionRequest a su DTO de respuesta.
     */
    private InstitutionRequestResponse mapToResponse(InstitutionRequest request) {
        InstitutionRequestResponse.InstitutionRequestResponseBuilder builder = InstitutionRequestResponse.builder()
            .id(request.getId())
            .institutionName(request.getInstitutionName())
            .legalAddress(request.getLegalAddress())
            .documentUrl(request.getDocumentUrl())
            .officialPhone(request.getOfficialPhone())
            .type(request.getType())
            .officialRegistryNumber(request.getOfficialRegistryNumber())
            .officialWebsite(request.getOfficialWebsite())
            .status(request.getStatus())
            .rejectionReason(request.getRejectionReason())
            .createdAt(request.getCreatedAt())
            .reviewedAt(request.getReviewedAt());

        // Agregar información del revisor si existe
        if (request.getReviewedBy() != null) {
            builder.reviewedBy(UserDto.builder()
                .id(request.getReviewedBy().getId())
                .name(request.getReviewedBy().getName())
                .build());
        }

        return builder.build();
    }

    /**
     * Valida el documento subido.
     */
    private void validateDocument(MultipartFile document) {
        if (document == null || document.isEmpty()) {
            throw new BadRequestException("El documento es requerido");
        }

        // Validar tamaño
        if (document.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("El documento no puede ser mayor a 5MB");
        }

        // Validar tipo de contenido
        String contentType = document.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Tipo de documento no permitido. Solo se permiten: PDF, JPG, PNG");
        }

        // Validar nombre del archivo
        String originalFilename = document.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BadRequestException("El documento debe tener un nombre válido");
        }

        log.info("Documento validado exitosamente: {} - Tamaño: {} bytes - Tipo: {}", 
                 originalFilename, document.getSize(), contentType);
    }

    /**
     * Sube el documento a Cloudinary.
     */
    private String uploadDocument(MultipartFile document, Long userId) {
        try {
            // Configurar opciones de upload específicas para documentos institucionales
            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("folder", "institution-documents/" + userId);
            uploadOptions.put("resource_type", "auto"); // Permite PDF e imágenes
            uploadOptions.put("use_filename", true);
            uploadOptions.put("unique_filename", true);

            // Subir a Cloudinary
            Map<String, Object> result = cloudinaryService.upload(document, uploadOptions);
            String documentUrl = (String) result.get("url");

            log.info("Documento institucional subido exitosamente para usuario: {} - URL: {}", 
                     userId, documentUrl);

            return documentUrl;

        } catch (Exception e) {
            log.error("Error al subir documento institucional para usuario: {}", userId, e);
            throw new BadRequestException("Error al subir el documento: " + e.getMessage());
        }
    }
}
