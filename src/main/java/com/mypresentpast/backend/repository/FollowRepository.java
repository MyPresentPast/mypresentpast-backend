package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.model.Follow;
import com.mypresentpast.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operaciones relacionadas con Follow.
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * Busca una relación de seguimiento específica.
     */
    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    /**
     * Verifica si un usuario sigue a otro.
     */
    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    /**
     * Obtiene todos los seguidores de un usuario.
     */
    @Query("SELECT f.follower FROM Follow f WHERE f.followee.id = :userId")
    List<User> findFollowersByUserId(@Param("userId") Long userId);

    /**
     * Obtiene todos los usuarios que sigue un usuario.
     */
    @Query("SELECT f.followee FROM Follow f WHERE f.follower.id = :userId")
    List<User> findFollowingByUserId(@Param("userId") Long userId);

    /**
     * Cuenta el número de seguidores de un usuario.
     */
    long countByFolloweeId(Long followeeId);

    /**
     * Cuenta el número de usuarios que sigue un usuario.
     */
    long countByFollowerId(Long followerId);

}