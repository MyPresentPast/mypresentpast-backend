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
}
