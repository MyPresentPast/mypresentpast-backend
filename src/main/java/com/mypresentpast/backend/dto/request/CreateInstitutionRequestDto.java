package com.mypresentpast.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mypresentpast.backend.enums.InstitutionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear una nueva solicitud de institución.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstitutionRequestDto {

    /**
     * Nombre oficial de la institución.
     */
    @JsonProperty("institution_name")
    @NotBlank()
    @Size(min = 2, max = 100)
    private String institutionName;

    /**
     * Dirección legal de la institución.
     */
    @JsonProperty("legal_address")
    @NotBlank()
    @Size(min = 10, max = 200)
    private String legalAddress;

    /**
     * Teléfono oficial de la institución.
     */
    @JsonProperty("official_phone")
    @NotBlank()
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", 
             message = "Teléfono debe tener formato internacional válido")
    private String officialPhone;

    /**
     * Tipo de institución.
     */
    @NotNull()
    private InstitutionType type;

    /**
     * Número de registro oficial (opcional).
     */
    @JsonProperty("official_registry_number")
    @Size(max = 50)
    private String officialRegistryNumber;

    /**
     * Sitio web oficial (opcional).
     */
    @JsonProperty("official_website")
    @Size(max = 200)
    @Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/.*)?$|^$", 
             message = "Sitio web debe tener formato de URL válido")
    private String officialWebsite;
}
