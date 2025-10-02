package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.model.CollectionPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para operaciones con CollectionPost.
 */
@Repository
public interface CollectionPostRepository extends JpaRepository<CollectionPost, Long> {

    /**
     * Busca la relación entre una colección y un post.
     */
    Optional<CollectionPost> findByCollectionIdAndPostId(Long collectionId, Long postId);

    /**
     * Verifica si un post ya está en una colección.
     */
    boolean existsByCollectionIdAndPostId(Long collectionId, Long postId);

    /**
     * Cuenta el número de posts en una colección.
     */
    long countByCollectionId(Long collectionId);

    /**
     * Obtiene todos los posts de una colección ordenados por fecha de agregado.
     */
    List<CollectionPost> findByCollectionIdOrderByAddedAtDesc(Long collectionId);

    /**
     * Obtiene las colecciones donde está guardado un post para un usuario específico.
     */
    @Query("SELECT cp FROM CollectionPost cp " +
           "WHERE cp.post.id = :postId " +
           "AND cp.collection.author.id = :authorId " +
           "ORDER BY cp.collection.name")
    List<CollectionPost> findByPostIdAndCollectionAuthorId(@Param("postId") Long postId, @Param("authorId") Long authorId);

    /**
     * Elimina la relación entre una colección y un post.
     */
    void deleteByCollectionIdAndPostId(Long collectionId, Long postId);
}
