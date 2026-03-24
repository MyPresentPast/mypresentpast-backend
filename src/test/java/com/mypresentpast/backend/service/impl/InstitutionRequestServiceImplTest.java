package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.CreateInstitutionRequestDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.InstitutionRequestResponse;
import com.mypresentpast.backend.enums.InstitutionType;
import com.mypresentpast.backend.enums.RequestStatus;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.model.InstitutionRequest;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.InstitutionRequestRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.service.CloudinaryService;
import com.mypresentpast.backend.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionRequestServiceImplTest {

    // ── Mocks ──────────────────────────────────────────────────────────────
    @Mock
    private InstitutionRequestRepository institutionRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private InstitutionRequestServiceImpl institutionRequestService;

    // ── Datos de test reutilizables ────────────────────────────────────────
    private User testUser;
    private InstitutionRequest testInstitutionRequest;
    private CreateInstitutionRequestDto testRequestDto;

    // ── Setup ──────────────────────────────────────────────────────────────
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setProfileUsername("testuser");
        testUser.setRole(UserRole.NORMAL);
        testUser.setEmailVerified(true);

        testInstitutionRequest = InstitutionRequest.builder()
                .id(10L)
                .user(testUser)
                .institutionName("Museo Nacional")
                .legalAddress("Av. Siempre Viva 742, Springfield")
                .officialPhone("+5491112345678")
                .type(InstitutionType.MUSEUM)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.of(2026, 1, 15, 10, 0))
                .build();

        testRequestDto = CreateInstitutionRequestDto.builder()
                .institutionName("Museo Nacional")
                .legalAddress("Av. Siempre Viva 742, Springfield")
                .officialPhone("+5491112345678")
                .type(InstitutionType.MUSEUM)
                .build();
    }

    // ── getMyRequests ──────────────────────────────────────────────────────

    // Verifica que las solicitudes del usuario actual se retornan correctamente mapeadas a DTOs.
    @Test
    void getMyRequests_WithRequests_ReturnsMappedResponseList() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(institutionRequestRepository.findByUserIdOrderByCreatedAtDesc(1L))
                    .thenReturn(List.of(testInstitutionRequest));

            List<InstitutionRequestResponse> result = institutionRequestService.getMyRequests();

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(10L, result.get(0).getId());
            assertEquals("Museo Nacional", result.get(0).getInstitutionName());
            assertEquals(RequestStatus.PENDING, result.get(0).getStatus());
            verify(institutionRequestRepository).findByUserIdOrderByCreatedAtDesc(1L);
        }
    }

    // Verifica que si el usuario no tiene solicitudes se retorna una lista vacía.
    @Test
    void getMyRequests_NoRequests_ReturnsEmptyList() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(institutionRequestRepository.findByUserIdOrderByCreatedAtDesc(1L))
                    .thenReturn(List.of());

            List<InstitutionRequestResponse> result = institutionRequestService.getMyRequests();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ── createRequest ──────────────────────────────────────────────────────

    // Verifica que una solicitud válida se persiste correctamente y retorna el mensaje de éxito.
    @Test
    void createRequest_ValidRequest_SavesAndReturnsSuccessMessage() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            MultipartFile document = validDocumentMock();
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(institutionRequestRepository.hasActiveRequest(1L)).thenReturn(false);
            when(cloudinaryService.upload(eq(document), any(Map.class)))
                    .thenReturn(Map.of("url", "https://cloudinary.com/doc.pdf"));

            ApiResponse response = institutionRequestService.createRequest(testRequestDto, document);

            assertNotNull(response);
            assertTrue(response.getMessage().contains("creada exitosamente"));

            ArgumentCaptor<InstitutionRequest> captor = ArgumentCaptor.forClass(InstitutionRequest.class);
            verify(institutionRequestRepository).save(captor.capture());
            InstitutionRequest saved = captor.getValue();
            assertEquals(testUser, saved.getUser());
            assertEquals("Museo Nacional", saved.getInstitutionName());
            assertEquals("https://cloudinary.com/doc.pdf", saved.getDocumentUrl());
            assertEquals(RequestStatus.PENDING, saved.getStatus());
            assertNotNull(saved.getCreatedAt());
        }
    }

    // Verifica que si el usuario actual no existe en la DB se lanza ResourceNotFoundException.
    @Test
    void createRequest_UserNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> institutionRequestService.createRequest(testRequestDto, mock(MultipartFile.class)));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que un usuario con rol distinto a NORMAL no puede crear una solicitud.
    @Test
    void createRequest_UserNotNormal_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            testUser.setRole(UserRole.INSTITUTION);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            assertThrows(BadRequestException.class,
                    () -> institutionRequestService.createRequest(testRequestDto, mock(MultipartFile.class)));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que no se puede crear una solicitud si el usuario ya tiene una activa.
    @Test
    void createRequest_HasActiveRequest_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(institutionRequestRepository.hasActiveRequest(1L)).thenReturn(true);

            assertThrows(BadRequestException.class,
                    () -> institutionRequestService.createRequest(testRequestDto, mock(MultipartFile.class)));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que pasar un documento nulo lanza BadRequestException.
    @Test
    void createRequest_NullDocument_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(institutionRequestRepository.hasActiveRequest(1L)).thenReturn(false);

            assertThrows(BadRequestException.class,
                    () -> institutionRequestService.createRequest(testRequestDto, null));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que un documento mayor a 5MB lanza BadRequestException.
    @Test
    void createRequest_DocumentTooLarge_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(institutionRequestRepository.hasActiveRequest(1L)).thenReturn(false);

            MultipartFile document = mock(MultipartFile.class);
            when(document.isEmpty()).thenReturn(false);
            when(document.getSize()).thenReturn(6 * 1024 * 1024L); // 6MB

            assertThrows(BadRequestException.class,
                    () -> institutionRequestService.createRequest(testRequestDto, document));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que un documento con tipo de contenido no permitido lanza BadRequestException.
    @Test
    void createRequest_InvalidContentType_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(institutionRequestRepository.hasActiveRequest(1L)).thenReturn(false);

            MultipartFile document = mock(MultipartFile.class);
            when(document.isEmpty()).thenReturn(false);
            when(document.getSize()).thenReturn(1024L);
            when(document.getContentType()).thenReturn("text/plain");

            assertThrows(BadRequestException.class,
                    () -> institutionRequestService.createRequest(testRequestDto, document));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que un documento sin nombre de archivo lanza BadRequestException.
    @Test
    void createRequest_NoFilename_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(institutionRequestRepository.hasActiveRequest(1L)).thenReturn(false);

            MultipartFile document = mock(MultipartFile.class);
            when(document.isEmpty()).thenReturn(false);
            when(document.getSize()).thenReturn(1024L);
            when(document.getContentType()).thenReturn("application/pdf");
            when(document.getOriginalFilename()).thenReturn("");

            assertThrows(BadRequestException.class,
                    () -> institutionRequestService.createRequest(testRequestDto, document));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // ── cancelRequest ──────────────────────────────────────────────────────

    // Verifica que cancelar una solicitud PENDING actualiza su estado a CANCELLED y retorna el mensaje correcto.
    @Test
    void cancelRequest_PendingRequest_UpdatesStatusAndReturnsMessage() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(institutionRequestRepository.findByIdAndUserId(10L, 1L))
                    .thenReturn(Optional.of(testInstitutionRequest));

            ApiResponse response = institutionRequestService.cancelRequest(10L);

            assertNotNull(response);
            assertTrue(response.getMessage().contains("cancelada exitosamente"));
            assertEquals(RequestStatus.CANCELLED, testInstitutionRequest.getStatus());
            assertNotNull(testInstitutionRequest.getReviewedAt());
            verify(institutionRequestRepository).save(testInstitutionRequest);
        }
    }

    // Verifica que cancelar una solicitud inexistente o de otro usuario lanza ResourceNotFoundException.
    @Test
    void cancelRequest_RequestNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(institutionRequestRepository.findByIdAndUserId(99L, 1L))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> institutionRequestService.cancelRequest(99L));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que cancelar una solicitud que no está en estado PENDING lanza BadRequestException.
    @Test
    void cancelRequest_NotPendingRequest_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            testInstitutionRequest.setStatus(RequestStatus.APPROVED);
            when(institutionRequestRepository.findByIdAndUserId(10L, 1L))
                    .thenReturn(Optional.of(testInstitutionRequest));

            assertThrows(BadRequestException.class,
                    () -> institutionRequestService.cancelRequest(10L));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // ── canCreateNewRequest ────────────────────────────────────────────────

    // Verifica que un usuario NORMAL sin solicitud activa puede crear una nueva.
    @Test
    void canCreateNewRequest_NormalUserWithoutActiveRequest_ReturnsTrue() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(institutionRequestRepository.hasActiveRequest(1L)).thenReturn(false);

            assertTrue(institutionRequestService.canCreateNewRequest());
        }
    }

    // Verifica que un usuario con rol distinto a NORMAL no puede crear una solicitud.
    @Test
    void canCreateNewRequest_UserNotNormal_ReturnsFalse() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            testUser.setRole(UserRole.INSTITUTION);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            assertFalse(institutionRequestService.canCreateNewRequest());
        }
    }

    // Verifica que un usuario NORMAL con solicitud activa no puede crear una nueva.
    @Test
    void canCreateNewRequest_HasActiveRequest_ReturnsFalse() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(institutionRequestRepository.hasActiveRequest(1L)).thenReturn(true);

            assertFalse(institutionRequestService.canCreateNewRequest());
        }
    }

    // Verifica que si el usuario no existe en la DB se retorna false.
    @Test
    void canCreateNewRequest_UserNotFound_ReturnsFalse() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertFalse(institutionRequestService.canCreateNewRequest());
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Crea un mock de MultipartFile con datos válidos para el camino feliz.
     */
    private MultipartFile validDocumentMock() {
        MultipartFile document = mock(MultipartFile.class);
        when(document.isEmpty()).thenReturn(false);
        when(document.getSize()).thenReturn(1024L);
        when(document.getContentType()).thenReturn("application/pdf");
        when(document.getOriginalFilename()).thenReturn("document.pdf");
        return document;
    }
}
