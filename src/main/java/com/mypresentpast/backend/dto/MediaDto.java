package com.mypresentpast.backend.dto;

import com.mypresentpast.backend.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for media response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto {

    private Long id;
    private MediaType type;
    private String url;
} 