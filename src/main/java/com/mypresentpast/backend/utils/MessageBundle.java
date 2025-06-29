package com.mypresentpast.backend.utils;

public class MessageBundle {
    public static final String VALIDATION_ERROR = "Error de validación";
    public static final String INTERNAL_SERVER_ERROR = "Error interno del servidor";
    public static final String UNAUTHORIZED = "No tenés permisos para realizar esta acción";
    public static final String RESOURCE_NOT_FOUND = "El recurso solicitado no fue encontrado";
    public static final String BAD_CREDENTIALS = "Credenciales inválidas";
    public static final String BAD_CREDENTIALS_DETAIL = "El email o la contraseña son incorrectos";
    public static final String DUPLICATE_EMAIL = "El email ya está registrado: %s";
    public static final String DUPLICATE_USERNAME = "El nombre de usuario ya está en uso: %s";

    // SQL
    public static final String REGISTER_CONFLICT = "Registro inválido";

    private MessageBundle() {
        // Evita instanciación
    }
}
