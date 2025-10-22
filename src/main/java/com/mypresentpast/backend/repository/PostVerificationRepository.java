package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.model.PostVerification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVerificationRepository extends JpaRepository<PostVerification, Long> {

    /**
     * Busca la verificación activa de un post específico.
     * Solo puede haber una verificación activa por post.
     */
    @Query("SELECT pv FROM PostVerification pv WHERE pv.post.id = :postId AND pv.isActive = true")
    Optional<PostVerification> findActiveByPostId(@Param("postId") Long postId);

    /**
     * Verifica si existe una verificación activa para un post.
     */
    @Query("SELECT COUNT(pv) > 0 FROM PostVerification pv WHERE pv.post.id = :postId AND pv.isActive = true")
    boolean existsActiveByPostId(@Param("postId") Long postId);

    /**
     * Busca la verificación activa de un post realizada por un usuario específico.
     * Útil para verificar si una institución puede desverificar un post que verificó.
     */
    @Query("SELECT pv FROM PostVerification pv WHERE pv.post.id = :postId AND pv.verifiedBy.id = :userId AND pv.isActive = true")
    Optional<PostVerification> findActiveByPostIdAndVerifiedBy(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * Busca todas las verificaciones de un post con usuario verificador cargado.
     */
    @Query("SELECT pv FROM PostVerification pv " +
           "LEFT JOIN FETCH pv.verifiedBy " +
           "WHERE pv.post.id = :postId")
    List<PostVerification> findByPostIdWithVerifier(@Param("postId") Long postId);
}
