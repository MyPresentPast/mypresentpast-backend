package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.enums.RequestStatus;
import com.mypresentpast.backend.model.InstitutionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones de base de datos con InstitutionRequest.
 */
@Repository
public interface InstitutionRequestRepository extends JpaRepository<InstitutionRequest, Long> {

    /**
     * Busca todas las solicitudes de un usuario ordenadas por fecha de creación descendente.
     */
    List<InstitutionRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Busca una solicitud activa (PENDING, IN_REVIEW o APPROVED) de un usuario.
     * Un usuario solo puede tener una solicitud activa a la vez.
     */
    @Query("SELECT ir FROM InstitutionRequest ir WHERE ir.user.id = :userId " +
           "AND ir.status IN ('PENDING', 'IN_REVIEW', 'APPROVED')")
    Optional<InstitutionRequest> findActiveRequestByUserId(@Param("userId") Long userId);

    /**
     * Verifica si un usuario tiene una solicitud activa.
     */
    @Query("SELECT COUNT(ir) > 0 FROM InstitutionRequest ir WHERE ir.user.id = :userId " +
           "AND ir.status IN ('PENDING', 'IN_REVIEW', 'APPROVED')")
    boolean hasActiveRequest(@Param("userId") Long userId);

    /**
     * Busca una solicitud por ID y usuario (para verificar ownership).
     */
    Optional<InstitutionRequest> findByIdAndUserId(Long id, Long userId);

    /**
     * Busca solicitudes por estado, ordenadas por fecha de creación.
     */
    List<InstitutionRequest> findByStatusOrderByCreatedAtAsc(RequestStatus status);

    /**
     * Busca todas las solicitudes con información del usuario, ordenadas por fecha.
     */
    @Query("SELECT ir FROM InstitutionRequest ir " +
           "LEFT JOIN FETCH ir.user " +
           "LEFT JOIN FETCH ir.reviewedBy " +
           "ORDER BY ir.createdAt DESC")
    List<InstitutionRequest> findAllWithUserInfo();

    /**
     * Cuenta solicitudes por estado.
     */
    long countByStatus(RequestStatus status);
}
