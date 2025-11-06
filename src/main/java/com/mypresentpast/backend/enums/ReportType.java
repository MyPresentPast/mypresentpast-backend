package com.mypresentpast.backend.enums;

/**
 * Tipos predefinidos de reporte para clasificar la razón del mismo.
 */
public enum ReportType {
    SPAM,        // Contenido no deseado / promocional
    ABUSE,       // Abuso, contenido ofensivo
    COPYRIGHT,   // Infracción de derechos de autor
    MISINFORMATION, // Información engañosa
    OTHER        // Otro tipo no listado
}
