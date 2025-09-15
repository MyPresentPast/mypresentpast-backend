package com.mypresentpast.backend.model;

import com.mypresentpast.backend.enums.InstitutionType;
import com.mypresentpast.backend.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa una solicitud para convertirse en institución.
 */
@Entity
@Table(name = "institution_request")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario que realiza la solicitud.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull()
    private User user;

    /**
     * Nombre oficial de la institución.
     */
    @Column(name = "institution_name", nullable = false, length = 100)
    @NotBlank()
    @Size(max = 100)
    private String institutionName;

    /**
     * Dirección legal de la institución.
     */
    @Column(name = "legal_address", nullable = false, length = 200)
    @NotBlank()
    @Size(max = 200)
    private String legalAddress;

    /**
     * URL del documento de validación subido a Cloudinary.
     */
    @Column(name = "document_url", nullable = false, length = 500)
    @NotBlank()
    private String documentUrl;

    /**
     * Teléfono oficial de la institución.
     */
    @Column(name = "official_phone", nullable = false, length = 20)
    @NotBlank()
    @Size(max = 20)
    private String officialPhone;

    /**
     * Tipo de institución.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull()
    private InstitutionType type;

    /**
     * Número de registro oficial (opcional).
     */
    @Column(name = "official_registry_number", length = 50)
    @Size(max = 50)
    private String officialRegistryNumber;

    /**
     * Sitio web oficial (opcional).
     */
    @Column(name = "official_website", length = 200)
    @Size(max = 200)
    private String officialWebsite;

    /**
     * Estado actual de la solicitud.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    /**
     * Motivo de rechazo (solo si status = REJECTED).
     */
    @Column(name = "rejection_reason", length = 500)
    @Size(max = 500)
    private String rejectionReason;

    /**
     * Fecha y hora de creación de la solicitud.
     */
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Fecha y hora de revisión de la solicitud.
     */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    /**
     * Administrador que revisó la solicitud.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;
}
