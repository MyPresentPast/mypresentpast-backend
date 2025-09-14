package com.mypresentpast.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectRequestDto {
    
    @NotBlank(message = "El motivo de rechazo es obligatorio")
    @Size(min = 10, max = 500)
    @JsonProperty("rejection_reason")
    private String rejectionReason;
}
