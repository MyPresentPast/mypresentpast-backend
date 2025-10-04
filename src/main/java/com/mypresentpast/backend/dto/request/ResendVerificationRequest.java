package com.mypresentpast.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendVerificationRequest {
    private String email;
}
