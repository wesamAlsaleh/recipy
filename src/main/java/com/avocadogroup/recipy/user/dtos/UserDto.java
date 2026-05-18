package com.avocadogroup.recipy.user.dtos;

import com.avocadogroup.recipy.user.UserRole;

import java.time.Instant;

public record UserDto(
        Long id,
        String username,
        String email,
        UserRole role,
        Boolean status,
        String verified,
        Instant createdAt
) {
}
