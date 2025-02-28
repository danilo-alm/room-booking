package com.danilo.roombooking.dto;

import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.authority.Authority;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponseDTO(Long id, String username, String fullName, String email,
                              Timestamp createdAt, Timestamp updatedAt, Timestamp lastLogin,
                              Integer failedLoginAttempts, Boolean enabled, Set<String> authorities) {
    public UserResponseDTO(User user) {
        this(
            user.getId(),
            user.getUsername(),
            user.getFullName(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getLastLogin(),
            user.getFailedLoginAttempts(),
            user.getEnabled(),
            user.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toSet())
        );
    }
}
