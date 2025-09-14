package com.mypresentpast.backend.enums;

/**
 * Estados posibles de una solicitud de institución.
 */
public enum RequestStatus {
    /**
     * Solicitud recién creada, pendiente de revisión por un administrador.
     */
    PENDING,
    
    /**
     * Solicitud aprobada. El usuario se convierte en INSTITUTION automáticamente.
     */
    APPROVED,
    
    /**
     * Solicitud rechazada con motivo especificado.
     * El usuario puede crear una nueva solicitud.
     */
    REJECTED,
    
    /**
     * Solicitud cancelada por el usuario (solo si estaba PENDING).
     */
    CANCELLED
}
