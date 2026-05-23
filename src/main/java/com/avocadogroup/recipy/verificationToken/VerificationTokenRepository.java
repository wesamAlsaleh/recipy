package com.avocadogroup.recipy.verificationToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    // TODO: fix n + 1 (user is lazy loaded)
    Optional<VerificationToken> findByToken(String token);
}
