package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.model.Media;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para operaciones con Media.
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    /**
     * Buscar imágenes disponibles (sin post asignado) ordenadas por ID.
     */
    @Query("SELECT m FROM Media m WHERE m.post IS NULL ORDER BY m.id ASC")
    List<Media> findAvailableMedia();
} 