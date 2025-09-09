package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operaciones relacionadas con PostLike.
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * Busca un like específico por usuario y post.
     */
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

    /**
     * Verifica si un usuario dio like a un post.
     */
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    /**
     * Cuenta el número total de likes de un post.
     */
    long countByPostId(Long postId);

    /**
     * Cuenta el número de posts que le gustan a un usuario.
     */
    long countByUserId(Long userId);

    /**
     * Elimina un like específico por usuario y post.
     */
    void deleteByUserIdAndPostId(Long userId, Long postId);

    /**
     * Obtiene estadísticas de likes para múltiples posts.
     * Útil para optimizar consultas en listas de posts.
     */
    @Query("SELECT pl.post.id, COUNT(pl) FROM PostLike pl WHERE pl.post.id IN :postIds GROUP BY pl.post.id")
    Object[][] countLikesByPostIds(@Param("postIds") java.util.List<Long> postIds);
}
