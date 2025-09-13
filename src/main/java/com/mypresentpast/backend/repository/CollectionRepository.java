package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.model.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para operaciones con Collection.
 */
@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    /**
     * Busca todas las colecciones de un usuario.
     */
    List<Collection> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    /**
     * Cuenta el número de colecciones de un usuario.
     */
    long countByAuthorId(Long authorId);

    /**
     * Busca una colección por ID y autor.
     */
    Optional<Collection> findByIdAndAuthorId(Long id, Long authorId);

    /**
     * Obtiene las colecciones de un usuario con el conteo de posts.
     */
    @Query("SELECT c, COUNT(cp) as postCount " +
           "FROM Collection c LEFT JOIN c.collectionPosts cp " +
           "WHERE c.author.id = :authorId " +
           "GROUP BY c.id " +
           "ORDER BY c.createdAt DESC")
    List<Object[]> findCollectionsWithPostCountByAuthorId(@Param("authorId") Long authorId);

    /**
     * Verifica si existe una colección con el mismo nombre para un usuario (case sensitive).
     */
    boolean existsByNameAndAuthorId(String name, Long authorId);

    /**
     * Verifica si existe una colección con el mismo nombre para un usuario, ignorando mayúsculas/minúsculas.
     */
    boolean existsByNameIgnoreCaseAndAuthorId(String name, Long authorId);

    /**
     * Verifica si existe una colección con el mismo nombre para un usuario, excluyendo una colección específica (case sensitive).
     */
    boolean existsByNameAndAuthorIdAndIdNot(String name, Long authorId, Long collectionId);

    /**
     * Verifica si existe una colección con el mismo nombre para un usuario, excluyendo una colección específica, ignorando mayúsculas/minúsculas.
     */
    boolean existsByNameIgnoreCaseAndAuthorIdAndIdNot(String name, Long authorId, Long collectionId);
}
