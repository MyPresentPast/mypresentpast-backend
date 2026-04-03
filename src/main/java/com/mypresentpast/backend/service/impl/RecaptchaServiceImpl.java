package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.service.RecaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class RecaptchaServiceImpl implements RecaptchaService {

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.verify-url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean verify(String token) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", token);

            Map response = restTemplate.postForObject(verifyUrl, params, Map.class);

            boolean success = Boolean.TRUE.equals(response != null ? response.get("success") : false);
            log.info("reCAPTCHA verification result: {}", success);
            return success;
        } catch (Exception e) {
            log.error("Error verifying reCAPTCHA: {}", e.getMessage());
            return false;
        }
    }
}
