package com.mypresentpast.backend.service.ai;

/**
 * Interfaz para proveedores de IA.
 * Permite cambiar fácilmente entre diferentes motores de IA (OpenAI, Claude, Gemini, etc.)
 */
public interface AIProvider {

    /**
     * Corrige el contenido usando el motor de IA específico.
     *
     * @param content el contenido a corregir
     * @return el contenido corregido
     */
    String correctContent(String content);

    /**
     * Genera contenido para una publicación usando el motor de IA específico.
     *
     * @param date la fecha de la publicación
     * @param location la ubicación (dirección)
     * @param context el contexto proporcionado por el usuario
     * @return el contenido generado en formato JSON con título, contenido y categoría
     */
    String generatePostContent(String date, String location, String context);

    /**
     * Obtiene el nombre del proveedor.
     *
     * @return el nombre del proveedor (ej: "OpenAI", "Demo", "Claude")
     */
    String getProviderName();
} 