package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para operaciones con User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
