package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.enums.PostStatus;
import com.mypresentpast.backend.model.Post;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para operaciones con Post.
 * Extiende JpaRepository para operaciones CRUD básicas.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Query única y elegante con filtros opcionales.
     */
    @Query(value = "SELECT p.* FROM posts p " +
        "JOIN locations l ON p.location_id = l.id " +
        "WHERE l.latitude BETWEEN :latMin AND :latMax " +
        "AND l.longitude BETWEEN :lonMin AND :lonMax " +
        "AND p.status = 'ACTIVE' " +
        "AND (NULLIF(:category, '') IS NULL OR p.category = :category) " +
        "AND (CAST(:date AS DATE) IS NULL OR p.date = CAST(:date AS DATE)) " +
        "ORDER BY p.posted_at DESC",
        nativeQuery = true)
    List<Post> findPostsInAreaWithFilters(
        @Param("latMin") double latMin,
        @Param("latMax") double latMax,
        @Param("lonMin") double lonMin,
        @Param("lonMax") double lonMax,
        @Param("category") String category,
        @Param("date") LocalDate date
    );

    /**
     * Busca posts activos que tengan ubicación para el endpoint random.
     */
    List<Post> findByStatusAndLocationIsNotNull(PostStatus status);
}