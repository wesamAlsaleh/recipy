package com.avocadogroup.recipy.userSession;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionsRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByToken(String token);

    List<UserSession> findAllByUserId(Long userId);
}
