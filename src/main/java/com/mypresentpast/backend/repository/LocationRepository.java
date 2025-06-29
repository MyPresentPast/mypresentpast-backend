package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.model.Location;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Busca ubicaciones existentes dentro de un rango de proximidad.
     * Utiliza una tolerancia de 0.001 grados (~111 metros) para considerar ubicaciones iguales.
     */
    @Query("SELECT l FROM Location l WHERE " +
        "(l.latitude BETWEEN :latitude - 0.001 AND :latitude + 0.001) AND " +
        "(l.longitude BETWEEN :longitude - 0.001 AND :longitude + 0.001)")
    List<Location> findLocationsByProximity(@Param("latitude") Double latitude,
                                            @Param("longitude") Double longitude);
} 