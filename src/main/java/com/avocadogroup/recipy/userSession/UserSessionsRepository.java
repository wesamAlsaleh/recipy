package com.avocadogroup.recipy.userSession;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionsRepository extends CrudRepository<UserSession, Long> {
    Optional<UserSession> findByToken(String token);

    List<UserSession> findAllByUserId(Long userId);
}
