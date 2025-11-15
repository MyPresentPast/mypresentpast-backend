package com.mypresentpast.backend.enums;

/**
 * Estado del procesamiento del reporte:
 */
public enum ReportStatus {
    PENDING, // pendiente, ningún admin lo tomó.
    ACCEPTED, // se aprueba y se debe remover la publicación.
    REJECTED // se rechaza y no se hace nada.
}
