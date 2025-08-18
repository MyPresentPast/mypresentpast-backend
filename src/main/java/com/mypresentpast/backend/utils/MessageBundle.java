package com.mypresentpast.backend.utils;

public class MessageBundle {
    public static final String VALIDATION_ERROR = "Error de validación";
    public static final String INTERNAL_SERVER_ERROR = "Error interno del servidor";
    public static final String UNAUTHORIZED = "No tenés permisos para realizar esta acción";
    public static final String RESOURCE_NOT_FOUND = "El recurso solicitado no fue encontrado";
    public static final String BAD_CREDENTIALS = "Credenciales inválidas";
    public static final String BAD_REQUEST = "Solicitud inválida";
    public static final String BAD_CREDENTIALS_DETAIL = "El email o la contraseña son incorrectos";

    // SQL
    public static final String REGISTER_CONFLICT = "Registro inválido";

    // Registro y Login
    public static final String PASSWORD_MISMATCH = "Las contraseñas no coinciden.";
    public static final String PASSWORD_INVALID = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.";
    public static final String CURRENT_PASSWORD_INVALID = "Credenciales inválidas.";
    public static final String NEW_PASSWORD_SAME_AS_OLD = "La nueva contraseña no puede ser igual a la actual.";
    public static final String DUPLICATE_EMAIL = "El email ya está registrado: [%s]";
    public static final String DUPLICATE_USERNAME = "El nombre de usuario ya está en uso: %s";

    // Profile
    public static final String USER_NOT_FOUND_WITH_ID = "Usuario no encontrado con id [%s] ";
    public static final String AVATAR_FILE_REQUIRED = "La imagen es obligatoria para actualizar el avatar";
    public static final String AVATAR_FILE_TOO_LARGE = "La imagen seleccionada supera el tamaño máximo permitido de %d MB.";
    public static final String AVATAR_FILE_INVALID_TYPE = "El formato de la imagen no es válido. Solo se permiten archivos JPEG, PNG o WEBP.";

    // Cloudinary
    public static final String CLOUDINARY_UPLOAD_ERROR = "Ha ocurrido un error al subir la imagen a Cloudinary. Inténtalo nuevamente más tarde.";


    private MessageBundle() {
        // Evita instanciación
    }
}
