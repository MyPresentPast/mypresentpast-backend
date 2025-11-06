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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInstitutionRequestResponse {
    
    private Long id;
    
    @JsonProperty("type")
    private InstitutionType type;
    
    @JsonProperty("status")
    private RequestStatus status;
    
    @JsonProperty("user")
    private UserDto user;  // Información completa del usuario solicitante
    
    @JsonProperty("institution_name")
    private String institutionName;
    
    @JsonProperty("legal_address")
    private String legalAddress;
    
    @JsonProperty("document_url")
    private String documentUrl;
    
    @JsonProperty("official_phone")
    private String officialPhone;
    
    @JsonProperty("official_registry_number")
    private String officialRegistryNumber;
    
    @JsonProperty("official_website")
    private String officialWebsite;
    
    @JsonProperty("rejection_reason")
    private String rejectionReason;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("reviewed_at")
    private LocalDateTime reviewedAt;
    
    @JsonProperty("reviewed_by")
    private UserDto reviewedBy;  // Admin que procesó la solicitud
}
