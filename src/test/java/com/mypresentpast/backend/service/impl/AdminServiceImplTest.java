package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.response.AdminInstitutionRequestResponse;
import com.mypresentpast.backend.enums.InstitutionType;
import com.mypresentpast.backend.enums.RequestStatus;
import com.mypresentpast.backend.exception.BadRequestException;
import com.mypresentpast.backend.exception.ResourceNotFoundException;
import com.mypresentpast.backend.exception.UnauthorizedException;
import com.mypresentpast.backend.model.InstitutionRequest;
import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.UserRole;
import com.mypresentpast.backend.repository.InstitutionRequestRepository;
import com.mypresentpast.backend.repository.UserRepository;
import com.mypresentpast.backend.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    // ── Mocks ──────────────────────────────────────────────────────────────
    @Mock
    private InstitutionRequestRepository institutionRequestRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    // ── Datos de test reutilizables ────────────────────────────────────────
    private User adminUser;
    private User requestUser;
    private InstitutionRequest testRequest;

    // ── Setup ──────────────────────────────────────────────────────────────
    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(99L);
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setName("Admin");

        requestUser = new User();
        requestUser.setId(1L);
        requestUser.setRole(UserRole.NORMAL);
        requestUser.setName("John");
        requestUser.setAvatar("avatar.jpg");

        testRequest = InstitutionRequest.builder()
                .id(10L)
                .user(requestUser)
                .institutionName("Museo Nacional")
                .legalAddress("Av. Siempre Viva 742, Springfield")
                .documentUrl("https://cloudinary.com/doc.pdf")
                .officialPhone("+5491112345678")
                .type(InstitutionType.MUSEUM)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.of(2026, 1, 15, 10, 0))
                .build();
    }

    // ── getAllRequests ─────────────────────────────────────────────────────

    // Verifica que con filtro de estado se llama findByStatusWithUserInfo y se mapean los resultados.
    @Test
    void getAllRequests_WithStatusFilter_ReturnsFilteredMappedList() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findByStatusWithUserInfo(RequestStatus.PENDING))
                    .thenReturn(List.of(testRequest));

            List<AdminInstitutionRequestResponse> result = adminService.getAllRequests(RequestStatus.PENDING);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(10L, result.get(0).getId());
            assertEquals("Museo Nacional", result.get(0).getInstitutionName());
            assertEquals(RequestStatus.PENDING, result.get(0).getStatus());
            verify(institutionRequestRepository).findByStatusWithUserInfo(RequestStatus.PENDING);
            verify(institutionRequestRepository, never()).findAllWithUserInfo();
        }
    }

    // Verifica que sin filtro de estado se llama findAllWithUserInfo y se mapean todos los resultados.
    @Test
    void getAllRequests_WithoutStatusFilter_ReturnsAllMappedRequests() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findAllWithUserInfo()).thenReturn(List.of(testRequest));

            List<AdminInstitutionRequestResponse> result = adminService.getAllRequests(null);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(institutionRequestRepository).findAllWithUserInfo();
            verify(institutionRequestRepository, never()).findByStatusWithUserInfo(any());
        }
    }

    // Verifica que si no hay solicitudes se retorna una lista vacía.
    @Test
    void getAllRequests_NoRequests_ReturnsEmptyList() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findAllWithUserInfo()).thenReturn(List.of());

            List<AdminInstitutionRequestResponse> result = adminService.getAllRequests(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // ── getRequestDetail ───────────────────────────────────────────────────

    // Verifica que buscar el detalle de una solicitud existente retorna la respuesta correctamente mapeada.
    @Test
    void getRequestDetail_ExistingId_ReturnsMappedResponse() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findByIdWithUserInfo(10L))
                    .thenReturn(Optional.of(testRequest));

            AdminInstitutionRequestResponse result = adminService.getRequestDetail(10L);

            assertNotNull(result);
            assertEquals(10L, result.getId());
            assertEquals("Museo Nacional", result.getInstitutionName());
            assertEquals(RequestStatus.PENDING, result.getStatus());
            assertNotNull(result.getUser());
            assertEquals(1L, result.getUser().getId());
        }
    }

    // Verifica que buscar el detalle de una solicitud inexistente lanza ResourceNotFoundException.
    @Test
    void getRequestDetail_NotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findByIdWithUserInfo(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> adminService.getRequestDetail(99L));
        }
    }

    // ── approveRequest ─────────────────────────────────────────────────────

    // Verifica que aprobar una solicitud PENDING cambia su estado a APPROVED y el rol del usuario a INSTITUTION.
    @Test
    void approveRequest_ValidPendingRequest_ApprovesAndChangesUserRoleToInstitution() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findByIdWithUserInfo(10L))
                    .thenReturn(Optional.of(testRequest));
            when(userRepository.findById(99L)).thenReturn(Optional.of(adminUser));

            adminService.approveRequest(10L);

            assertEquals(RequestStatus.APPROVED, testRequest.getStatus());
            assertEquals(adminUser, testRequest.getReviewedBy());
            assertNotNull(testRequest.getReviewedAt());
            assertNull(testRequest.getRejectionReason());
            assertEquals(UserRole.INSTITUTION, requestUser.getRole());
            verify(institutionRequestRepository).save(testRequest);
            verify(userRepository).save(requestUser);
        }
    }

    // Verifica que aprobar una solicitud inexistente lanza ResourceNotFoundException.
    @Test
    void approveRequest_RequestNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findByIdWithUserInfo(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> adminService.approveRequest(99L));

            verify(institutionRequestRepository, never()).save(any());
            verify(userRepository, never()).save(any());
        }
    }

    // Verifica que aprobar una solicitud que no está en estado PENDING lanza BadRequestException.
    @Test
    void approveRequest_RequestNotPending_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            testRequest.setStatus(RequestStatus.APPROVED);
            when(institutionRequestRepository.findByIdWithUserInfo(10L))
                    .thenReturn(Optional.of(testRequest));

            assertThrows(BadRequestException.class, () -> adminService.approveRequest(10L));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que aprobar una solicitud cuyo usuario ya es INSTITUTION lanza BadRequestException.
    @Test
    void approveRequest_UserAlreadyInstitution_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            requestUser.setRole(UserRole.INSTITUTION);
            when(institutionRequestRepository.findByIdWithUserInfo(10L))
                    .thenReturn(Optional.of(testRequest));

            assertThrows(BadRequestException.class, () -> adminService.approveRequest(10L));

            verify(userRepository, never()).save(any());
        }
    }

    // Verifica que no se puede convertir a un ADMIN en institución.
    @Test
    void approveRequest_UserIsAdmin_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            requestUser.setRole(UserRole.ADMIN);
            when(institutionRequestRepository.findByIdWithUserInfo(10L))
                    .thenReturn(Optional.of(testRequest));

            assertThrows(BadRequestException.class, () -> adminService.approveRequest(10L));

            verify(userRepository, never()).save(any());
        }
    }

    // Verifica que si el admin actual no existe en la DB se lanza UnauthorizedException.
    @Test
    void approveRequest_AdminNotFound_ThrowsUnauthorizedException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findByIdWithUserInfo(10L))
                    .thenReturn(Optional.of(testRequest));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UnauthorizedException.class, () -> adminService.approveRequest(10L));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // ── rejectRequest ──────────────────────────────────────────────────────

    // Verifica que rechazar una solicitud PENDING con motivo válido la marca como REJECTED y guarda el motivo.
    @Test
    void rejectRequest_ValidPendingRequest_RejectsWithReason() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            String reason = "La documentación presentada no cumple con los requisitos mínimos.";
            when(institutionRequestRepository.findByIdWithUserInfo(10L))
                    .thenReturn(Optional.of(testRequest));
            when(userRepository.findById(99L)).thenReturn(Optional.of(adminUser));

            adminService.rejectRequest(10L, reason);

            assertEquals(RequestStatus.REJECTED, testRequest.getStatus());
            assertEquals(reason.trim(), testRequest.getRejectionReason());
            assertEquals(adminUser, testRequest.getReviewedBy());
            assertNotNull(testRequest.getReviewedAt());
            verify(institutionRequestRepository).save(testRequest);
            verify(userRepository, never()).save(any());
        }
    }

    // Verifica que rechazar sin motivo (null) lanza BadRequestException.
    @Test
    void rejectRequest_NullReason_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);

            assertThrows(BadRequestException.class, () -> adminService.rejectRequest(10L, null));

            verify(institutionRequestRepository, never()).findByIdWithUserInfo(any());
        }
    }

    // Verifica que rechazar con motivo vacío lanza BadRequestException.
    @Test
    void rejectRequest_EmptyReason_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);

            assertThrows(BadRequestException.class, () -> adminService.rejectRequest(10L, "   "));

            verify(institutionRequestRepository, never()).findByIdWithUserInfo(any());
        }
    }

    // Verifica que un motivo de menos de 10 caracteres lanza BadRequestException.
    @Test
    void rejectRequest_ReasonTooShort_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);

            assertThrows(BadRequestException.class, () -> adminService.rejectRequest(10L, "Corto"));

            verify(institutionRequestRepository, never()).findByIdWithUserInfo(any());
        }
    }

    // Verifica que un motivo de más de 500 caracteres lanza BadRequestException.
    @Test
    void rejectRequest_ReasonTooLong_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            String longReason = "x".repeat(501);

            assertThrows(BadRequestException.class, () -> adminService.rejectRequest(10L, longReason));

            verify(institutionRequestRepository, never()).findByIdWithUserInfo(any());
        }
    }

    // Verifica que rechazar una solicitud inexistente lanza ResourceNotFoundException.
    @Test
    void rejectRequest_RequestNotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findByIdWithUserInfo(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> adminService.rejectRequest(99L, "Motivo de rechazo válido con longitud correcta."));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que rechazar una solicitud que no está en estado PENDING lanza BadRequestException.
    @Test
    void rejectRequest_RequestNotPending_ThrowsBadRequestException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            testRequest.setStatus(RequestStatus.APPROVED);
            when(institutionRequestRepository.findByIdWithUserInfo(10L))
                    .thenReturn(Optional.of(testRequest));

            assertThrows(BadRequestException.class,
                    () -> adminService.rejectRequest(10L, "Motivo de rechazo válido con longitud correcta."));

            verify(institutionRequestRepository, never()).save(any());
        }
    }

    // Verifica que si el admin actual no existe en la DB se lanza UnauthorizedException.
    @Test
    void rejectRequest_AdminNotFound_ThrowsUnauthorizedException() {
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
            when(institutionRequestRepository.findByIdWithUserInfo(10L))
                    .thenReturn(Optional.of(testRequest));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(UnauthorizedException.class,
                    () -> adminService.rejectRequest(10L, "Motivo de rechazo válido con longitud correcta."));

            verify(institutionRequestRepository, never()).save(any());
        }
    }
}
