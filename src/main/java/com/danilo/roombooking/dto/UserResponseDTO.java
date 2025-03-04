package com.danilo.roombooking.dto;

import com.danilo.roombooking.domain.User;
import com.danilo.roombooking.domain.role.Role;
import com.danilo.roombooking.domain.role.RoleType;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponseDTO(Long id, String username, String fullName, String email,
                              Timestamp createdAt, Timestamp updatedAt, Timestamp lastLogin,
                              Integer failedLoginAttempts, Boolean enabled, Set<RoleType> roles) {
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
            user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }
}
