package com.mypresentpast.backend.repository;

import com.mypresentpast.backend.model.User;
import com.mypresentpast.backend.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);
    void deleteByUser(User user);

}
