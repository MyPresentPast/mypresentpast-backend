package com.mypresentpast.backend.exception;

public class CloudinaryException extends RuntimeException {
    public CloudinaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
