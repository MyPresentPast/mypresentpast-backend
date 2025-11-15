package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con Post.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Query única y elegante con filtros opcionales.
     * Incluye filtros por etiquetas (verificada y hecha por IA) y usuario.
     */
    @Query(value = "SELECT p.* FROM post p " +
        "JOIN location l ON p.location_id = l.id " +
        "WHERE l.latitude BETWEEN :latMin AND :latMax " +
        "AND l.longitude BETWEEN :lonMin AND :lonMax " +
        "AND p.status = 'ACTIVE' " +
        "AND (NULLIF(:category, '') IS NULL OR p.category = :category) " +
        "AND p.date >= COALESCE(:dateFrom, DATE '1000-01-01') " +
        "AND p.date <= COALESCE(:dateTo, DATE '2100-12-31') " +
        "AND (:isByIA IS NULL OR p.is_by_ia = :isByIA) " +
        "AND (:userId IS NULL OR p.author_id = :userId) " +
        "ORDER BY p.posted_at DESC",
        nativeQuery = true)
    List<Post> findPostsInAreaWithFilters(
        @Param("latMin") double latMin,
        @Param("latMax") double latMax,
        @Param("lonMin") double lonMin,
        @Param("lonMax") double lonMax,
        @Param("category") String category,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        @Param("isByIA") Boolean isByIA,
        @Param("userId") Long userId
    );

    /**
     * Query con soporte para múltiples categorías y usuarios (filtros OR).
     * Las categorías se filtran como OR: (cat1 OR cat2 OR cat3)
     * Los usuarios se filtran como OR: (user1 OR user2 OR user3)
     */
    @Query(value = "SELECT p.* FROM post p " +
        "JOIN location l ON p.location_id = l.id " +
        "WHERE l.latitude BETWEEN :latMin AND :latMax " +
        "AND l.longitude BETWEEN :lonMin AND :lonMax " +
        "AND p.status = 'ACTIVE' " +
        "AND (COALESCE(:#{#categoryStrings.size()}, 0) = 0 OR p.category IN (:categoryStrings)) " +
        "AND p.date >= COALESCE(:dateFrom, DATE '1000-01-01') " +
        "AND p.date <= COALESCE(:dateTo, DATE '2100-12-31') " +
        "AND (:isByIA IS NULL OR p.is_by_ia = :isByIA) " +
        "AND (COALESCE(:#{#userIds.size()}, 0) = 0 OR p.author_id IN (:userIds)) " +
        "ORDER BY p.posted_at DESC",
        nativeQuery = true)
    List<Post> findPostsInAreaWithMultipleFilters(
        @Param("latMin") double latMin,
        @Param("latMax") double latMax,
        @Param("lonMin") double lonMin,
        @Param("lonMax") double lonMax,
        @Param("categoryStrings") List<String> categoryStrings,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        @Param("isByIA") Boolean isByIA,
        @Param("userIds") List<Long> userIds
    );

    /**
     * Busca posts activos que tengan ubicación para el endpoint random.
     */
    List<Post> findByStatusAndLocationIsNotNull(PostStatus status);

    /**
     * Busca posts activos por usuario.
     */
    List<Post> findByAuthorIdAndStatus(Long authorId, PostStatus status);
    @Query("SELECT p FROM Post p " +
           "LEFT JOIN FETCH p.author " +
           "WHERE p.author.id = :authorId")
    List<Post> findByAuthorId(@Param("authorId") Long id);

    /**
     * Busca un post por ID con autor y ubicación cargados.
     */
    @Query("SELECT p FROM Post p " +
           "LEFT JOIN FETCH p.author " +
           "LEFT JOIN FETCH p.location " +
           "WHERE p.id = :id AND p.status = 'ACTIVE'")
    Optional<Post> findByIdWithRelations(@Param("id") Long id);


    /**
     * Cuenta el número de posts activos de un usuario.
     */
    long countByAuthorIdAndStatus(Long authorId, PostStatus status);

    /**
     * Obtiene los posts que un usuario ha likeado, ordenados por fecha de like (más recientes primero).
     */
    @Query(value = "SELECT p.* FROM post p " +
        "INNER JOIN post_like pl ON p.id = pl.post_id " +
        "WHERE pl.user_id = :userId " +
        "AND p.status = 'ACTIVE' " +
        "ORDER BY pl.created_at DESC",
        nativeQuery = true)
    List<Post> findLikedPostsByUserId(@Param("userId") Long userId);
}