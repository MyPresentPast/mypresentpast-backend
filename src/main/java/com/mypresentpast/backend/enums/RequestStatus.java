package com.mypresentpast.backend.enums;

/**
 * Estados posibles de una solicitud de institución.
 */
public enum RequestStatus {
    /**
     * Solicitud recién creada, pendiente de revisión.
     */
    PENDING,
    
    /**
     * Solicitud en proceso de revisión por un administrador.
     * Evita que múltiples admins revisen la misma solicitud.
     */
    IN_REVIEW,
    
    /**
     * Solicitud aprobada. El usuario se convierte en INSTITUTION.
     */
    APPROVED,
    
    /**
     * Solicitud rechazada con motivo especificado.
     */
    REJECTED,
    
    /**
     * Solicitud cancelada por el usuario (solo si estaba PENDING).
     */
    CANCELLED
}
