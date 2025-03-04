package com.danilo.roombooking.dto;

import com.danilo.roombooking.domain.role.RoleType;

import java.util.Set;

public record UserRequestDTO(String username, String password, String fullName, Boolean enabled, String email,
                             Set<RoleType> roles) {
}
