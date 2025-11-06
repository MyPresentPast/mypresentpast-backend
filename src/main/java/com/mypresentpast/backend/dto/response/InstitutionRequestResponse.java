package com.mypresentpast.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.enums.InstitutionType;
import com.mypresentpast.backend.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para solicitudes de institución.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionRequestResponse {

    /**
     * ID único de la solicitud.
     */
    private Long id;

    /**
     * Nombre oficial de la institución.
     */
    @JsonProperty("institution_name")
    private String institutionName;

    /**
     * Dirección legal de la institución.
     */
    @JsonProperty("legal_address")
    private String legalAddress;

    /**
     * URL del documento de validación.
     */
    @JsonProperty("document_url")
    private String documentUrl;

    /**
     * Teléfono oficial de la institución.
     */
    @JsonProperty("official_phone")
    private String officialPhone;

    /**
     * Tipo de institución.
     */
    private InstitutionType type;

    /**
     * Número de registro oficial (opcional).
     */
    @JsonProperty("official_registry_number")
    private String officialRegistryNumber;

    /**
     * Sitio web oficial (opcional).
     */
    @JsonProperty("official_website")
    private String officialWebsite;

    /**
     * Estado actual de la solicitud.
     */
    private RequestStatus status;

    /**
     * Motivo de rechazo (solo si status = REJECTED).
     */
    @JsonProperty("rejection_reason")
    private String rejectionReason;

    /**
     * Fecha y hora de creación de la solicitud.
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de revisión de la solicitud.
     */
    @JsonProperty("reviewed_at")
    private LocalDateTime reviewedAt;

    /**
     * Administrador que revisó la solicitud (solo para admins).
     */
    @JsonProperty("reviewed_by")
    private UserDto reviewedBy;

    /**
     * Usuario que realizó la solicitud (solo para admins).
     */
    private UserDto user;
}
