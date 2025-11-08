package com.mypresentpast.backend.enums;

/**
 * Estado del procesamiento del reporte:
 */
public enum ReportStatus {
    PENDING, // pendiente, ningún admin lo tomó.
    IN_PROGRESS, // un admin lo está revisando.
    ACCEPTED, // se aprueba y se debe remover la publicación (otra tarea).
    REJECTED // se rechaza y no se hace nada.
}
