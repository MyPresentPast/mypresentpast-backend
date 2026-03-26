package com.mypresentpast.backend.service;

public interface RecaptchaService {
    boolean verify(String token);
}
