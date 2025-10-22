package com.mypresentpast.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {
    private String recipient;
    private String subject;
    private String name;
    private String verificationUrl;

}
